package org.richardstallman.dvback.domain.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.firebase.service.FirebaseMessagingService;
import org.richardstallman.dvback.client.python.PythonService;
import org.richardstallman.dvback.common.constant.CommonConstants.EvaluationCriteria;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationDto;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationScoreConverter;
import org.richardstallman.dvback.domain.evaluation.converter.EvaluationCriteriaConverter;
import org.richardstallman.dvback.domain.evaluation.converter.OverallEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.EvaluationCriteriaDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalAnswerRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalAnswersRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionsRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultCriteriaDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultRequestDto;
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
  private final FirebaseMessagingService firebaseMessagingService;

  @Override
  public void getOverallEvaluation(
      OverallEvaluationRequestDto overallEvaluationRequestDto, Long userId) {
    List<QuestionDomain> questions = retrieveQuestions(overallEvaluationRequestDto.interviewId());
    InterviewDomain interviewDomain = questions.get(0).getInterviewDomain();

    List<String> filePaths = new ArrayList<>();
    if (interviewDomain.getInterviewMode() == InterviewMode.REAL) {
      filePaths.add(interviewDomain.getCoverLetter().getS3FileUrl());
    }
    callPythonEvaluationService(userId, questions, filePaths);
    log.info("Request Evaluation To Python: Success");
  }

  @Override
  public OverallEvaluationResponseDto getOverallEvaluationByInterviewId(Long interviewId) {
    InterviewDomain interviewDomain = interviewService.getInterviewById(interviewId);
    OverallEvaluationDomain overallEvaluationDomain =
        OverallEvaluationDomain.builder()
            .overallEvaluationId(
                getOverallEvaluationDomainByInterviewId(interviewId).getOverallEvaluationId())
            .interviewDomain(interviewDomain)
            .build();
    List<AnswerEvaluationDomain> answerEvaluationDomains =
        answerEvaluationRepository.findByInterviewId(interviewId);
    return buildResponseDto(interviewDomain, overallEvaluationDomain, answerEvaluationDomains);
  }

  @Override
  public OverallEvaluationDomain getOverallEvaluationDomainById(Long overallEvaluationId) {
    return overallEvaluationRepository.findById(overallEvaluationId);
  }

  @Override
  public void saveCompletedEvaluation(OverallEvaluationResultRequestDto evaluationResult) {
    Long userId = evaluationResult.userId();
    Long interviewId = evaluationResult.interviewId();
    OverallEvaluationResultDto overallEvaluationResultDto = evaluationResult.overallEvaluation();

    OverallEvaluationDomain overallEvaluationDomain =
        getOverallEvaluationDomainByInterviewId(interviewId);
    saveEvaluationCriteriaResult(overallEvaluationDomain, overallEvaluationResultDto);

    firebaseMessagingService.sendNotification(
        userId, "평가가 완료되었습니다.", evaluationResult.interviewId().toString());
  }

  private void saveEvaluationCriteriaResult(
      OverallEvaluationDomain overallEvaluationDomain,
      OverallEvaluationResultDto overallEvaluationResultDto) {
    Map<EvaluationCriteria, OverallEvaluationResultCriteriaDto> criteriaMap =
        Map.of(
            EvaluationCriteria.JOB_FIT,
            overallEvaluationResultDto.textOverall().jobFit(),
            EvaluationCriteria.GROWTH_POTENTIAL,
            overallEvaluationResultDto.textOverall().growthPotential(),
            EvaluationCriteria.WORK_ATTITUDE,
            overallEvaluationResultDto.textOverall().workAttitude(),
            EvaluationCriteria.TECHNICAL_DEPTH,
            overallEvaluationResultDto.textOverall().technicalDepth());

    if (overallEvaluationDomain.getInterviewDomain().getInterviewMethod()
        == InterviewMethod.VOICE) {
      criteriaMap =
          Map.of(
              EvaluationCriteria.JOB_FIT,
              overallEvaluationResultDto.textOverall().jobFit(),
              EvaluationCriteria.GROWTH_POTENTIAL,
              overallEvaluationResultDto.textOverall().growthPotential(),
              EvaluationCriteria.WORK_ATTITUDE,
              overallEvaluationResultDto.textOverall().workAttitude(),
              EvaluationCriteria.TECHNICAL_DEPTH,
              overallEvaluationResultDto.textOverall().technicalDepth(),
              EvaluationCriteria.FLUENCY,
              overallEvaluationResultDto.voiceOverall().fluency(),
              EvaluationCriteria.CLARITY,
              overallEvaluationResultDto.voiceOverall().clarity(),
              EvaluationCriteria.WORD_REPETITION,
              overallEvaluationResultDto.voiceOverall().wordRepetition());
    }

    List<EvaluationCriteriaDomain> criteriaDomains =
        criteriaMap.entrySet().stream()
            .map(
                entry ->
                    EvaluationCriteriaDomain.builder()
                        .evaluationCriteria(entry.getKey())
                        .overallEvaluationDomain(overallEvaluationDomain)
                        .feedbackText(entry.getValue().rationale())
                        .score(entry.getValue().score())
                        .build())
            .toList();
    evaluationCriteriaRepository.saveAll(criteriaDomains);
  }

  private OverallEvaluationDomain getOverallEvaluationDomainByInterviewId(Long interviewId) {
    return overallEvaluationRepository.findByInterviewId(interviewId);
  }

  private List<QuestionDomain> retrieveQuestions(Long interviewId) {
    return questionRepository.findQuestionsByInterviewId(interviewId);
  }

  private void callPythonEvaluationService(
      Long userId, List<QuestionDomain> questions, List<String> filePaths) {
    List<EvaluationExternalQuestionRequestDto> questionTexts =
        questions.stream()
            .map(questionConverter::fromDomainToEvaluationExternalRequestDto)
            .toList();

    List<AnswerDomain> answerDomains =
        questions.stream()
            .map(question -> answerRepository.findByQuestionId(question.getQuestionId()))
            .toList();

    Map<AnswerDomain, AnswerEvaluationDto> answerMap =
        answerDomains.stream()
            .collect(
                Collectors.toMap(
                    answer -> answer,
                    answer ->
                        answerEvaluationConverter.fromDomainToDto(
                            answer,
                            answerEvaluationRepository.findByQuestionId(
                                answer.getQuestionDomain().getQuestionId()),
                            answerEvaluationScoreRepository.findByQuestionId(
                                answer.getQuestionDomain().getQuestionId()))));

    List<EvaluationExternalAnswerRequestDto> answerRequestDtos =
        answerDomains.stream()
            .map(
                e ->
                    new EvaluationExternalAnswerRequestDto(
                        e.getQuestionDomain().getQuestionId(), answerMap.get(e)))
            .toList();

    InterviewDomain interviewDomain = questions.get(0).getInterviewDomain();
    EvaluationExternalRequestDto requestDto =
        new EvaluationExternalRequestDto(
            userId,
            interviewDomain.getInterviewMode(),
            interviewDomain.getInterviewType(),
            interviewDomain.getInterviewMethod(),
            interviewDomain.getJob().getJobName(),
            new EvaluationExternalQuestionsRequestDto(questionTexts),
            new EvaluationExternalAnswersRequestDto(answerRequestDtos),
            filePaths);

    pythonService.getOverallEvaluations(requestDto);
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
