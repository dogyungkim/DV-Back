package org.richardstallman.dvback.domain.evaluation.converter;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationCriteriaDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationFeedbackDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationScoreDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationTextScoreDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationVoiceScoreDto;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationScoreDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.response.AnswerEvaluationScoreResponseDto;
import org.richardstallman.dvback.domain.evaluation.entity.answer.AnswerEvaluationEntity;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class AnswerEvaluationConverter {

  private final QuestionConverter questionConverter;
  private final OverallEvaluationConverter overallEvaluationConverter;

  @Autowired
  public AnswerEvaluationConverter(
      QuestionConverter questionConverter,
      @Lazy OverallEvaluationConverter overallEvaluationConverter) {
    this.questionConverter = questionConverter;
    this.overallEvaluationConverter = overallEvaluationConverter;
  }

  public AnswerEvaluationEntity fromDomainToEntity(AnswerEvaluationDomain answerEvaluationDomain) {
    return new AnswerEvaluationEntity(
        answerEvaluationDomain.getAnswerEvaluationId(),
        questionConverter.fromDomainToEntity(answerEvaluationDomain.getQuestionDomain()),
        answerEvaluationDomain.getAnswerFeedbackStrength(),
        answerEvaluationDomain.getAnswerFeedbackImprovement(),
        answerEvaluationDomain.getAnswerFeedbackSuggestion(),
        overallEvaluationConverter.fromDomainToEntity(
            answerEvaluationDomain.getOverallEvaluationDomain()));
  }

  public AnswerEvaluationDomain fromEntityToDomain(AnswerEvaluationEntity answerEvaluationEntity) {
    return AnswerEvaluationDomain.builder()
        .answerEvaluationId(answerEvaluationEntity.getAnswerEvaluationId())
        .questionDomain(questionConverter.fromEntityToDomain(answerEvaluationEntity.getQuestion()))
        .answerFeedbackStrength(answerEvaluationEntity.getAnswerFeedbackStrength())
        .answerFeedbackImprovement(answerEvaluationEntity.getAnswerFeedbackImprovement())
        .answerFeedbackSuggestion(answerEvaluationEntity.getAnswerFeedbackSuggestion())
        .overallEvaluationDomain(
            overallEvaluationConverter.fromEntityToDomain(
                answerEvaluationEntity.getOverallEvaluation()))
        .build();
  }

  public AnswerEvaluationResponseDto fromDomainToResponseDto(
      AnswerEvaluationDomain answerEvaluationDomain,
      String answerText,
      List<AnswerEvaluationScoreResponseDto> answerEvaluationScoreResponseDtos) {
    return new AnswerEvaluationResponseDto(
        answerEvaluationDomain.getAnswerEvaluationId(),
        answerEvaluationDomain.getQuestionDomain().getQuestionText(),
        answerText,
        answerEvaluationDomain.getAnswerFeedbackStrength(),
        answerEvaluationDomain.getAnswerFeedbackImprovement(),
        answerEvaluationDomain.getAnswerFeedbackSuggestion(),
        answerEvaluationScoreResponseDtos);
  }

  public AnswerEvaluationVoiceScoreDto toAnswerEvaluationVoiceScoreDto(
      List<AnswerEvaluationScoreDomain> answerEvaluationScoreDomainList) {
    AnswerEvaluationCriteriaDto wpm = null, stutter = null, pronunciation = null;
    for (AnswerEvaluationScoreDomain answerEvaluationScoreDomain :
        answerEvaluationScoreDomainList) {
      switch (answerEvaluationScoreDomain.getAnswerEvaluationScoreName()) {
        case WPM -> wpm =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case STUTTER -> stutter =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case PRONUNCIATION -> pronunciation =
            new AnswerEvaluationCriteriaDto(answerEvaluationScoreDomain.getScore(), null);
      }
    }
    return new AnswerEvaluationVoiceScoreDto(wpm, stutter, pronunciation);
  }

  public AnswerEvaluationDto fromDomainToDto(
      AnswerDomain answerDomain,
      AnswerEvaluationDomain answerEvaluationDomain,
      List<AnswerEvaluationScoreDomain> answerEvaluationScoreDomains) {
    return new AnswerEvaluationDto(
        answerDomain.getAnswerText(),
        answerDomain.getS3AudioUrl(),
        answerDomain.getS3VideoUrl(),
        new AnswerEvaluationScoreDto(
            answerDomain.getQuestionDomain().getInterviewDomain().getInterviewType()
                    == InterviewType.TECHNICAL
                ? toTechnicalTextScoreDto(answerEvaluationScoreDomains)
                : toPersonalTextScoreDto(answerEvaluationScoreDomains),
            answerDomain.getQuestionDomain().getInterviewDomain().getInterviewMethod()
                    == InterviewMethod.CHAT
                ? null
                : toAnswerEvaluationVoiceScoreDto(answerEvaluationScoreDomains)),
        new AnswerEvaluationFeedbackDto(
            answerEvaluationDomain.getAnswerFeedbackStrength(),
            answerEvaluationDomain.getAnswerFeedbackImprovement(),
            answerEvaluationDomain.getAnswerFeedbackSuggestion()));
  }

  private AnswerEvaluationTextScoreDto toTechnicalTextScoreDto(
      List<AnswerEvaluationScoreDomain> answerEvaluationScoreDomains) {
    AnswerEvaluationCriteriaDto appropriateResponse = null,
        logicalFlow = null,
        keyTerms = null,
        consistency = null,
        grammaticalErrors = new AnswerEvaluationCriteriaDto(0, ""),
        teamwork = new AnswerEvaluationCriteriaDto(0, ""),
        communication = new AnswerEvaluationCriteriaDto(0, ""),
        problemSolving = new AnswerEvaluationCriteriaDto(0, ""),
        accountability = new AnswerEvaluationCriteriaDto(0, ""),
        growthMindset = new AnswerEvaluationCriteriaDto(0, "");
    for (AnswerEvaluationScoreDomain answerEvaluationScoreDomain : answerEvaluationScoreDomains) {
      switch (answerEvaluationScoreDomain.getAnswerEvaluationScoreName()) {
        case APPROPRIATE_RESPONSE -> appropriateResponse =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case LOGICAL_FLOW -> logicalFlow =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case KEY_TERMS -> keyTerms =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case CONSISTENCY -> consistency =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case GRAMMATICAL_ERRORS -> grammaticalErrors =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
      }
    }
    return new AnswerEvaluationTextScoreDto(
        appropriateResponse,
        logicalFlow,
        keyTerms,
        consistency,
        grammaticalErrors,
        teamwork,
        communication,
        problemSolving,
        accountability,
        growthMindset);
  }

  private AnswerEvaluationTextScoreDto toPersonalTextScoreDto(
      List<AnswerEvaluationScoreDomain> answerEvaluationScoreDomains) {
    AnswerEvaluationCriteriaDto appropriateResponse = new AnswerEvaluationCriteriaDto(0, ""),
        logicalFlow = new AnswerEvaluationCriteriaDto(0, ""),
        keyTerms = new AnswerEvaluationCriteriaDto(0, ""),
        consistency = new AnswerEvaluationCriteriaDto(0, ""),
        grammaticalErrors = new AnswerEvaluationCriteriaDto(0, ""),
        teamwork = null,
        communication = null,
        problemSolving = null,
        accountability = null,
        growthMindset = null;
    for (AnswerEvaluationScoreDomain answerEvaluationScoreDomain : answerEvaluationScoreDomains) {
      switch (answerEvaluationScoreDomain.getAnswerEvaluationScoreName()) {
        case TEAMWORK -> teamwork =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case COMMUNICATION -> communication =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case PROBLEM_SOLVING -> problemSolving =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case ACCOUNTABILITY -> accountability =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
        case GROWTH_MINDSET -> growthMindset =
            new AnswerEvaluationCriteriaDto(
                answerEvaluationScoreDomain.getScore(), answerEvaluationScoreDomain.getRationale());
      }
    }
    return new AnswerEvaluationTextScoreDto(
        appropriateResponse,
        logicalFlow,
        keyTerms,
        consistency,
        grammaticalErrors,
        teamwork,
        communication,
        problemSolving,
        accountability,
        growthMindset);
  }

  public AnswerEvaluationDomain sttEvaluationFeedbackDomainToDomain(
      @NotNull AnswerEvaluationFeedbackDto feedback,
      AnswerDomain answerDomain,
      OverallEvaluationDomain overallEvaluationDomain) {
    return AnswerEvaluationDomain.builder()
        .questionDomain(answerDomain.getQuestionDomain())
        .answerFeedbackStrength(feedback.strengths())
        .answerFeedbackImprovement(feedback.improvement())
        .answerFeedbackSuggestion(feedback.suggestion())
        .overallEvaluationDomain(overallEvaluationDomain)
        .build();
  }
}
