package org.richardstallman.dvback.domain.question.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalDomain;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionInitialResponseDto;
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
        questionDomain.getKeyTerms(),
        questionDomain.getModelAnswer(),
        questionDomain.getQuestionIntent(),
        questionDomain.getS3AudioUrl(),
        questionDomain.getS3VideoUrl(),
        questionDomain.getQuestionType());
  }

  public QuestionDomain fromEntityToDomain(QuestionEntity questionEntity) {
    return QuestionDomain.builder()
        .questionId(questionEntity.getQuestionId())
        .interviewDomain(interviewConverter.fromEntityToDomain(questionEntity.getInterview()))
        .questionText(questionEntity.getQuestionText())
        .keyTerms(questionEntity.getKeyTerms())
        .modelAnswer(questionEntity.getModelAnswer())
        .questionIntent(questionEntity.getQuestionIntent())
        .s3AudioUrl(questionEntity.getS3AudioUrl())
        .s3VideoUrl(questionEntity.getS3VideoUrl())
        .questionType(questionEntity.getQuestionType())
        .build();
  }

  public QuestionDomain fromQuestionExternalDomainToDomain(
      QuestionExternalDomain questionExternalDomain, InterviewDomain interviewDomain) {
    return QuestionDomain.builder()
        .interviewDomain(interviewDomain)
        .questionText(questionExternalDomain.getQuestionText())
        .build();
  }

  public QuestionExternalRequestDto reactRequestToPythonRequest(
      QuestionInitialRequestDto questionInitialRequestDto, String jobName) {
    return new QuestionExternalRequestDto(
        questionInitialRequestDto.interviewMode() == InterviewMode.REAL
            ? questionInitialRequestDto.coverLetterS3Url()
            : null,
        questionInitialRequestDto.interviewMode(),
        questionInitialRequestDto.interviewType(),
        questionInitialRequestDto.interviewMethod(),
        jobName);
  }

  public QuestionInitialResponseDto fromQuestionExternalDomainToQuestionInitialResponseDto(
      QuestionExternalDomain questionExternalDomain,
      InterviewCreateResponseDto interviewCreateResponseDto,
      QuestionDomain questionDomain,
      Boolean hasNext) {
    return new QuestionInitialResponseDto(
        interviewCreateResponseDto,
        questionExternalDomain.getQuestionText(),
        questionDomain.getQuestionId(),
        hasNext);
  }
}
