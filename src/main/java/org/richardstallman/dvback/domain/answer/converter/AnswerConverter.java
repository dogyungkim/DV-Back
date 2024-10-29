package org.richardstallman.dvback.domain.answer.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerPreviousRequestDto;
import org.richardstallman.dvback.domain.answer.entity.AnswerEntity;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerConverter {

  private final QuestionConverter questionConverter;

  public AnswerEntity fromDomainToEntity(AnswerDomain answerDomain) {
    return new AnswerEntity(
        answerDomain.getAnswerId(),
        questionConverter.fromDomainToEntity(answerDomain.getQuestionDomain()),
        answerDomain.getAnswerText(),
        answerDomain.getS3AudioUrl(),
        answerDomain.getS3VideoUrl());
  }

  public AnswerDomain fromEntityToDomain(AnswerEntity answerEntity) {
    return AnswerDomain.builder()
        .answerId(answerEntity.getAnswerId())
        .questionDomain(questionConverter.fromEntityToDomain(answerEntity.getQuestion()))
        .answerText(answerEntity.getAnswerText())
        .s3AudioUrl(answerEntity.getS3AudioUrl())
        .s3VideoUrl(answerEntity.getS3VideoUrl())
        .build();
  }

  public AnswerDomain fromPreviousRequestDtoToDomain(
      AnswerPreviousRequestDto answerPreviousRequestDto, QuestionDomain questionDomain) {
    return AnswerDomain.builder()
        .questionDomain(questionDomain)
        .answerText(answerPreviousRequestDto.answerText())
        .s3AudioUrl(answerPreviousRequestDto.s3AudioUrl())
        .s3VideoUrl(questionDomain.getS3VideoUrl())
        .build();
  }
}
