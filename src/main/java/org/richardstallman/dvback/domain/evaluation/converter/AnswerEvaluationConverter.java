package org.richardstallman.dvback.domain.evaluation.converter;

import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.AnswerEvaluationExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.response.AnswerEvaluationScoreResponseDto;
import org.richardstallman.dvback.domain.evaluation.entity.answer.AnswerEvaluationEntity;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
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

  public AnswerEvaluationDomain externalDomainToDomain(
      AnswerEvaluationExternalDomain answerEvaluationExternalDomain,
      QuestionDomain questionDomain,
      OverallEvaluationDomain overallEvaluationDomain) {
    return AnswerEvaluationDomain.builder()
        .questionDomain(questionDomain)
        .answerFeedbackStrength(
            answerEvaluationExternalDomain
                .getAnswerEvaluationFeedbackExternalDomain()
                .getStrength())
        .answerFeedbackImprovement(
            answerEvaluationExternalDomain
                .getAnswerEvaluationFeedbackExternalDomain()
                .getImprovement())
        .answerFeedbackSuggestion(
            answerEvaluationExternalDomain
                .getAnswerEvaluationFeedbackExternalDomain()
                .getSuggestion())
        .overallEvaluationDomain(overallEvaluationDomain)
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
}
