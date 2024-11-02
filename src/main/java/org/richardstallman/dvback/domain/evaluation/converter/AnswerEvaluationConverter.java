package org.richardstallman.dvback.domain.evaluation.converter;

import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.AnswerEvaluationExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
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
        answerEvaluationDomain.getAnswerFeedbackText(),
        answerEvaluationDomain.getScore(),
        overallEvaluationConverter.fromDomainToEntity(
            answerEvaluationDomain.getOverallEvaluationDomain()));
  }

  public AnswerEvaluationDomain fromEntityToDomain(AnswerEvaluationEntity answerEvaluationEntity) {
    return AnswerEvaluationDomain.builder()
        .answerEvaluationId(answerEvaluationEntity.getAnswerEvaluationId())
        .questionDomain(questionConverter.fromEntityToDomain(answerEvaluationEntity.getQuestion()))
        .answerFeedbackText(answerEvaluationEntity.getAnswerFeedbackText())
        .score(answerEvaluationEntity.getScore())
        .build();
  }

  public AnswerEvaluationDomain externalDomainToDomain(
      AnswerEvaluationExternalDomain answerEvaluationExternalDomain,
      QuestionDomain questionDomain,
      OverallEvaluationDomain overallEvaluationDomain) {
    return AnswerEvaluationDomain.builder()
        .questionDomain(questionDomain)
        .answerFeedbackText(answerEvaluationExternalDomain.getFeedbackText())
        .score(answerEvaluationExternalDomain.getScore())
        .overallEvaluationDomain(overallEvaluationDomain)
        .build();
  }

  public AnswerEvaluationResponseDto fromDomainToResponseDto(
      AnswerEvaluationDomain answerEvaluationDomain, String answerText) {
    return new AnswerEvaluationResponseDto(
        answerEvaluationDomain.getAnswerEvaluationId(),
        answerEvaluationDomain.getQuestionDomain().getQuestionText(),
        answerText,
        answerEvaluationDomain.getAnswerFeedbackText(),
        answerEvaluationDomain.getScore());
  }
}
