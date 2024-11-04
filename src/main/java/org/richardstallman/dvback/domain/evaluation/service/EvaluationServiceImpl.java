package org.richardstallman.dvback.domain.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.python.PythonService;
import org.richardstallman.dvback.common.constant.CommonConstants.EvaluationCriteria;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.converter.EvaluationCriteriaConverter;
import org.richardstallman.dvback.domain.evaluation.converter.OverallEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.EvaluationCriteriaDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.AnswerEvaluationExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.external.EvaluationCriteriaExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.external.OverallEvaluationExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.response.EvaluationExternalResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.evaluation.repository.answer.AnswerEvaluationRepository;
import org.richardstallman.dvback.domain.evaluation.repository.criteria.EvaluationCriteriaRepository;
import org.richardstallman.dvback.domain.evaluation.repository.overall.OverallEvaluationRepository;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.repository.QuestionRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

  private final OverallEvaluationRepository overallEvaluationRepository;
  private final OverallEvaluationConverter overallEvaluationConverter;
  private final AnswerRepository answerRepository;
  private final QuestionRepository questionRepository;
  private final PythonService pythonService;
  private final AnswerEvaluationConverter answerEvaluationConverter;
  private final EvaluationCriteriaRepository evaluationCriteriaRepository;
  private final AnswerEvaluationRepository answerEvaluationRepository;
  private final EvaluationCriteriaConverter evaluationCriteriaConverter;

  @Override
  public OverallEvaluationResponseDto getOverallEvaluation(
      OverallEvaluationRequestDto overallEvaluationRequestDto) {

    List<QuestionDomain> questions =
        questionRepository.findQuestionsByInterviewId(overallEvaluationRequestDto.interviewId());
    List<String> questionTexts = new ArrayList<>();
    List<String> answerTexts = new ArrayList<>();
    for (QuestionDomain question : questions) {
      questionTexts.add(question.getQuestionText());
      answerTexts.add(answerRepository.findByQuestionId(question.getQuestionId()).getAnswerText());
    }

    InterviewDomain interviewDomain = questions.get(0).getInterviewDomain();

    EvaluationExternalRequestDto evaluationExternalRequestDto =
        new EvaluationExternalRequestDto(
            "", // 파일 업로드 및 주소 저장 연결부 구현 후 작성하기
            questionTexts,
            answerTexts,
            interviewDomain.getInterviewMode(),
            interviewDomain.getInterviewType(),
            interviewDomain.getInterviewMethod(),
            interviewDomain.getJob().getJobName());

    EvaluationExternalResponseDto evaluationExternalResponseDto =
        pythonService.getOverallEvaluations(evaluationExternalRequestDto);

    OverallEvaluationExternalDomain overallEvaluationExternalDomain =
        evaluationExternalResponseDto.overallEvaluation();
    List<AnswerEvaluationExternalDomain> answerEvaluationExternalDomains =
        evaluationExternalResponseDto.answerEvaluations();

    OverallEvaluationDomain createdOverallEvaluationDomain =
        overallEvaluationRepository.save(
            OverallEvaluationDomain.builder().interviewDomain(interviewDomain).build());

    Map<EvaluationCriteria, EvaluationCriteriaExternalDomain> criteriaMap =
        Map.of(
            EvaluationCriteria.DEVELOPMENT_SKILL,
            overallEvaluationExternalDomain.getDevelopmentSkill(),
            EvaluationCriteria.GROWTH_POTENTIAL,
            overallEvaluationExternalDomain.getGrowthPotential(),
            EvaluationCriteria.TECHNICAL_DEPTH,
            overallEvaluationExternalDomain.getTechnicalDepth(),
            EvaluationCriteria.WORK_ATTITUDE,
            overallEvaluationExternalDomain.getWorkAttitude());

    criteriaMap.forEach(
        (criteria, externalDomain) ->
            evaluationCriteriaRepository.save(
                EvaluationCriteriaDomain.builder()
                    .evaluationCriteria(criteria)
                    .overallEvaluationDomain(createdOverallEvaluationDomain)
                    .feedbackText(externalDomain.getFeedbackText())
                    .score(externalDomain.getScore())
                    .build()));

    List<AnswerEvaluationDomain> answerEvaluationDomains =
        answerEvaluationRepository.saveAll(
            answerEvaluationExternalDomains.stream()
                .map(
                    (e) ->
                        answerEvaluationConverter.externalDomainToDomain(
                            e,
                            questionRepository
                                .findById(e.getQuestionId())
                                .orElseThrow(
                                    () ->
                                        new ApiException(
                                            HttpStatus.NOT_FOUND,
                                            e.getQuestionId() + " does not exist")),
                            createdOverallEvaluationDomain))
                .toList());

    List<EvaluationCriteriaResponseDto> evaluationCriteriaResponseDtos =
        evaluationCriteriaRepository
            .findByOverallEvaluationId(createdOverallEvaluationDomain.getOverallEvaluationId())
            .stream()
            .map(evaluationCriteriaConverter::fromDomainToResponseDto)
            .toList();
    List<AnswerEvaluationResponseDto> answerEvaluationResponseDtos =
        answerEvaluationDomains.stream()
            .map(
                (e) ->
                    answerEvaluationConverter.fromDomainToResponseDto(
                        e,
                        answerRepository
                            .findByQuestionId(e.getQuestionDomain().getQuestionId())
                            .getAnswerText()))
            .toList();
    return overallEvaluationConverter.toResponseDto(
        interviewDomain, evaluationCriteriaResponseDtos, answerEvaluationResponseDtos);
  }
}
