package org.richardstallman.dvback.domain.question.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionRequestDto;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewQuestionResponseDto;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalDomain;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionResponseDto;
import org.richardstallman.dvback.domain.question.entity.QuestionEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionConverter {

  private final InterviewConverter interviewConverter;

  public QuestionEntity fromDomainToEntity(QuestionDomain questionDomain) {
    return new QuestionEntity(
        questionDomain.getQuestionId(),
        interviewConverter.fromDomainToEntity(questionDomain.getInterviewDomain()),
        questionDomain.getQuestionText(),
        questionDomain.getS3AudioUrl(),
        questionDomain.getS3VideoUrl(),
        questionDomain.getQuestionExcerpt(),
        questionDomain.getQuestionIntent(),
        questionDomain.getKeyTerms(),
        questionDomain.getQuestionType());
  }

  public QuestionDomain fromEntityToDomain(QuestionEntity questionEntity) {
    return QuestionDomain.builder()
        .questionId(questionEntity.getQuestionId())
        .interviewDomain(interviewConverter.fromEntityToDomain(questionEntity.getInterview()))
        .questionText(questionEntity.getQuestionText())
        .s3AudioUrl(questionEntity.getS3AudioUrl())
        .s3VideoUrl(questionEntity.getS3VideoUrl())
        .questionExcerpt(questionEntity.getQuestionExcerpt())
        .questionIntent(questionEntity.getQuestionIntent())
        .keyTerms(questionEntity.getKeyTerms())
        .questionType(questionEntity.getQuestionType())
        .build();
  }

  public QuestionDomain fromQuestionExternalDomainToDomain(
      QuestionExternalDomain questionExternalDomain, InterviewDomain interviewDomain) {
    return QuestionDomain.builder()
        .interviewDomain(interviewDomain)
        .questionText(questionExternalDomain.getQuestion().getQuestionText())
        .s3AudioUrl(questionExternalDomain.getQuestion().getS3AudioUrl())
        .s3VideoUrl(questionExternalDomain.getQuestion().getS3VideoUrl())
        .questionExcerpt(questionExternalDomain.getQuestionExcerpt())
        .questionIntent(questionExternalDomain.getQuestionIntent())
        .keyTerms(questionExternalDomain.getKeyTerms())
        .build();
  }

  public QuestionExternalRequestDto reactRequestToPythonRequest(
      Long userId, QuestionInitialRequestDto questionInitialRequestDto, String jobName) {
    return new QuestionExternalRequestDto(
        userId,
        questionInitialRequestDto.interviewMode(),
        questionInitialRequestDto.interviewType(),
        questionInitialRequestDto.interviewMethod(),
        questionInitialRequestDto.questionCount(),
        jobName,
        questionInitialRequestDto.files().stream()
            .map(FileRequestDto::getFilePath)
            .toArray(String[]::new));
  }

  public QuestionResponseDto fromQuestionExternalDomainToQuestionResponseDto(
      QuestionDomain firstQuestionDomain,
      InterviewQuestionResponseDto interviewQuestionResponseDto,
      QuestionDomain secondQuestionDomain,
      Boolean hasNext) {
    return new QuestionResponseDto(
        interviewQuestionResponseDto,
        firstQuestionDomain == null ? null : firstQuestionDomain.getQuestionId(),
        firstQuestionDomain == null ? null : firstQuestionDomain.getQuestionText(),
        firstQuestionDomain == null ? null : firstQuestionDomain.getS3AudioUrl(),
        secondQuestionDomain == null ? null : secondQuestionDomain.getQuestionId(),
        secondQuestionDomain == null ? null : secondQuestionDomain.getQuestionText(),
        secondQuestionDomain == null ? null : secondQuestionDomain.getS3AudioUrl(),
        hasNext);
  }

  public EvaluationExternalQuestionRequestDto fromDomainToEvaluationExternalRequestDto(
      QuestionDomain questionDomain) {
    return new EvaluationExternalQuestionRequestDto(
        questionDomain.getQuestionId(),
        new EvaluationExternalQuestionDto(
            questionDomain.getQuestionText(),
            questionDomain.getS3AudioUrl(),
            questionDomain.getS3VideoUrl()),
        questionDomain.getQuestionExcerpt(),
        questionDomain.getQuestionIntent(),
        questionDomain.getKeyTerms());
  }
}
