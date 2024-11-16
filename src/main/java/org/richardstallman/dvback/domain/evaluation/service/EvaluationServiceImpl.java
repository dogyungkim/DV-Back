package org.richardstallman.dvback.domain.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.python.PythonService;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore;
import org.richardstallman.dvback.common.constant.CommonConstants.EvaluationCriteria;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationScoreConverter;
import org.richardstallman.dvback.domain.evaluation.converter.EvaluationCriteriaConverter;
import org.richardstallman.dvback.domain.evaluation.converter.OverallEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.EvaluationCriteriaDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationScoreDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.AnswerEvaluationCriteriaExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.external.AnswerEvaluationExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.external.EvaluationCriteriaExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.external.OverallEvaluationExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalAnswerRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalAnswersRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionsRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.response.EvaluationExternalResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.evaluation.repository.answer.AnswerEvaluationRepository;
import org.richardstallman.dvback.domain.evaluation.repository.answer.score.AnswerEvaluationScoreRepository;
import org.richardstallman.dvback.domain.evaluation.repository.criteria.EvaluationCriteriaRepository;
import org.richardstallman.dvback.domain.evaluation.repository.overall.OverallEvaluationRepository;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
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
  private final AnswerEvaluationScoreRepository answerEvaluationScoreRepository;
  private final AnswerEvaluationScoreConverter answerEvaluationScoreConverter;
  private final CoverLetterConverter coverLetterConverter;
  private final InterviewService interviewService;
  private final QuestionConverter questionConverter;
  private final InterviewConverter interviewConverter;

  @Override
  public OverallEvaluationResponseDto getOverallEvaluation(
      OverallEvaluationRequestDto overallEvaluationRequestDto) {
    List<QuestionDomain> questions = retrieveQuestions(overallEvaluationRequestDto.interviewId());
    InterviewDomain interviewDomain = questions.get(0).getInterviewDomain();

    List<String> filePaths = new ArrayList<>();
    if (interviewDomain.getInterviewMode() == InterviewMode.REAL) {
      filePaths.add(interviewDomain.getCoverLetter().getS3FileUrl());
    }

    EvaluationExternalResponseDto evaluationExternalResponseDto =
        callPythonEvaluationService(questions, filePaths);

    OverallEvaluationDomain createdOverallEvaluationDomain = saveOverallEvaluation(interviewDomain);

    saveEvaluationCriteria(
        createdOverallEvaluationDomain, evaluationExternalResponseDto.overallEvaluation());

    List<AnswerEvaluationDomain> createdAnswerEvaluations =
        saveAnswerEvaluations(
            createdOverallEvaluationDomain, evaluationExternalResponseDto.answerEvaluations());

    return buildResponseDto(
        interviewDomain, createdOverallEvaluationDomain, createdAnswerEvaluations);
  }

  @Override
  public OverallEvaluationResponseDto getOverallEvaluationByInterviewId(Long interviewId) {
    InterviewDomain interviewDomain = interviewService.getInterviewById(interviewId);

    OverallEvaluationDomain overallEvaluationDomain =
        OverallEvaluationDomain.builder()
            .overallEvaluationId(
                overallEvaluationRepository.findByInterviewId(interviewId).getOverallEvaluationId())
            .interviewDomain(interviewDomain)
            .build();
    List<AnswerEvaluationDomain> answerEvaluationDomains =
        answerEvaluationRepository.findByInterviewId(interviewId);
    return buildResponseDto(interviewDomain, overallEvaluationDomain, answerEvaluationDomains);
  }

  private List<QuestionDomain> retrieveQuestions(Long interviewId) {
    return questionRepository.findQuestionsByInterviewId(interviewId);
  }

  private EvaluationExternalResponseDto callPythonEvaluationService(
      List<QuestionDomain> questions, List<String> filePaths) {
    List<EvaluationExternalQuestionRequestDto> questionTexts =
        questions.stream()
            .map(questionConverter::fromDomainToEvaluationExternalRequestDto)
            .toList();

    List<EvaluationExternalAnswerRequestDto> answerTexts =
        questions.stream()
            .map(
                question ->
                    new EvaluationExternalAnswerRequestDto(
                        question.getQuestionId(),
                        answerRepository
                            .findByQuestionId(question.getQuestionId())
                            .getAnswerText()))
            .toList();

    InterviewDomain interviewDomain = questions.get(0).getInterviewDomain();
    EvaluationExternalRequestDto requestDto =
        new EvaluationExternalRequestDto(
            interviewDomain.getInterviewMode(),
            interviewDomain.getInterviewType(),
            interviewDomain.getInterviewMethod(),
            interviewDomain.getJob().getJobName(),
            new EvaluationExternalQuestionsRequestDto(questionTexts),
            new EvaluationExternalAnswersRequestDto(answerTexts),
            filePaths);

    return pythonService.getOverallEvaluations(requestDto);
  }

  private OverallEvaluationDomain saveOverallEvaluation(InterviewDomain interviewDomain) {
    return overallEvaluationRepository.save(
        OverallEvaluationDomain.builder().interviewDomain(interviewDomain).build());
  }

  private void saveEvaluationCriteria(
      OverallEvaluationDomain overallEvaluation,
      OverallEvaluationExternalDomain externalEvaluation) {
    Map<EvaluationCriteria, EvaluationCriteriaExternalDomain> criteriaMap =
        Map.of(
            EvaluationCriteria.JOB_FIT, externalEvaluation.getJobFit(),
            EvaluationCriteria.GROWTH_POTENTIAL, externalEvaluation.getGrowthPotential(),
            EvaluationCriteria.WORK_ATTITUDE, externalEvaluation.getWorkAttitude(),
            EvaluationCriteria.TECHNICAL_DEPTH, externalEvaluation.getTechnicalDepth());

    List<EvaluationCriteriaDomain> criteriaDomains =
        criteriaMap.entrySet().stream()
            .map(
                entry ->
                    EvaluationCriteriaDomain.builder()
                        .evaluationCriteria(entry.getKey())
                        .overallEvaluationDomain(overallEvaluation)
                        .feedbackText(entry.getValue().getFeedbackText())
                        .score(entry.getValue().getScore())
                        .build())
            .toList();

    evaluationCriteriaRepository.saveAll(criteriaDomains);
  }

  private List<AnswerEvaluationDomain> saveAnswerEvaluations(
      OverallEvaluationDomain overallEvaluation,
      List<AnswerEvaluationExternalDomain> answerEvaluations) {
    List<AnswerEvaluationDomain> createdAnswerEvaluations = new ArrayList<>();

    for (AnswerEvaluationExternalDomain answerEvaluation : answerEvaluations) {
      AnswerEvaluationDomain answerEvaluationDomain =
          answerEvaluationRepository.save(
              answerEvaluationConverter.externalDomainToDomain(
                  answerEvaluation,
                  questionRepository
                      .findById(answerEvaluation.getQuestionId())
                      .orElseThrow(
                          () ->
                              new ApiException(
                                  HttpStatus.NOT_FOUND,
                                  "Question "
                                      + answerEvaluation.getQuestionId()
                                      + " does not exist")),
                  overallEvaluation));

      saveAnswerEvaluationScores(answerEvaluationDomain, answerEvaluation);
      createdAnswerEvaluations.add(answerEvaluationDomain);
    }

    return createdAnswerEvaluations;
  }

  private void saveAnswerEvaluationScores(
      AnswerEvaluationDomain answerEvaluationDomain,
      AnswerEvaluationExternalDomain answerEvaluation) {
    Map<AnswerEvaluationScore, AnswerEvaluationCriteriaExternalDomain> scoreMap =
        Map.of(
            AnswerEvaluationScore.APPROPRIATE_RESPONSE,
            answerEvaluation.getAnswerEvaluationScoreExternalDomain().getAppropriateResponse(),
            AnswerEvaluationScore.LOGICAL_FLOW,
            answerEvaluation.getAnswerEvaluationScoreExternalDomain().getLogicalFlow(),
            AnswerEvaluationScore.KEY_TERMS,
            answerEvaluation.getAnswerEvaluationScoreExternalDomain().getKeyTerms(),
            AnswerEvaluationScore.CONSISTENCY,
            answerEvaluation.getAnswerEvaluationScoreExternalDomain().getConsistency(),
            AnswerEvaluationScore.GRAMMATICAL_ERRORS,
            answerEvaluation.getAnswerEvaluationScoreExternalDomain().getGrammaticalErrors());

    List<AnswerEvaluationScoreDomain> scoreDomains =
        scoreMap.entrySet().stream()
            .map(
                entry ->
                    AnswerEvaluationScoreDomain.builder()
                        .answerEvaluationScoreName(entry.getKey())
                        .score(entry.getValue().getScore())
                        .rationale(entry.getValue().getRationale())
                        .answerEvaluationDomain(answerEvaluationDomain)
                        .build())
            .toList();

    answerEvaluationScoreRepository.saveAll(scoreDomains);
  }

  private OverallEvaluationResponseDto buildResponseDto(
      InterviewDomain interviewDomain,
      OverallEvaluationDomain overallEvaluation,
      List<AnswerEvaluationDomain> answerEvaluations) {

    List<EvaluationCriteriaResponseDto> criteriaResponseDtos =
        evaluationCriteriaRepository
            .findByOverallEvaluationId(overallEvaluation.getOverallEvaluationId())
            .stream()
            .map(evaluationCriteriaConverter::fromDomainToResponseDto)
            .toList();

    List<AnswerEvaluationResponseDto> answerEvaluationResponseDtos =
        answerEvaluations.stream()
            .map(
                e ->
                    answerEvaluationConverter.fromDomainToResponseDto(
                        e,
                        answerRepository
                            .findByQuestionId(e.getQuestionDomain().getQuestionId())
                            .getAnswerText(),
                        answerEvaluationScoreRepository
                            .findByAnswerEvaluationId(e.getAnswerEvaluationId())
                            .stream()
                            .map(answerEvaluationScoreConverter::fromDomainToResponseDto)
                            .toList()))
            .toList();

    List<FileResponseDto> fileResponseDtos = new ArrayList<>();

    if (interviewDomain.getInterviewMode() == InterviewMode.REAL) {
      fileResponseDtos.add(
          coverLetterConverter.fromDomainToResponseDto(interviewDomain.getCoverLetter()));
    }

    InterviewResponseDto interviewResponseDto =
        interviewConverter.fromDomainToResponseDto(interviewDomain, fileResponseDtos);

    return overallEvaluationConverter.toResponseDto(
        interviewResponseDto, criteriaResponseDtos, answerEvaluationResponseDtos);
  }
}
