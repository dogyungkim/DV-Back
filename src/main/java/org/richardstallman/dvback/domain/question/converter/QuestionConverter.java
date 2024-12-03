package org.richardstallman.dvback.domain.question.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionRequestDto;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewQuestionResponseDto;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionRequestListDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionResultDto;
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

  public QuestionExternalRequestDto reactRequestToPythonRequest(
      Long userId, QuestionRequestListDto questionRequestListDto, String jobName) {
    return new QuestionExternalRequestDto(
        userId,
        questionRequestListDto.interviewMode(),
        questionRequestListDto.interviewType(),
        questionRequestListDto.interviewMethod(),
        questionRequestListDto.questionCount(),
        jobName,
        questionRequestListDto.interviewMode() == InterviewMode.REAL
            ? questionRequestListDto.files().stream()
                .map(FileRequestDto::getFilePath)
                .toArray(String[]::new)
            : null);
  }

  public QuestionResponseDto generateResponseDto(
      InterviewQuestionResponseDto interviewQuestionResponseDto,
      QuestionDomain firstQuestion,
      QuestionDomain secondQuestion,
      Boolean hasNext) {
    return new QuestionResponseDto(
        interviewQuestionResponseDto,
        firstQuestion == null ? null : firstQuestion.getQuestionId(),
        firstQuestion == null ? null : firstQuestion.getQuestionText(),
        firstQuestion == null ? null : firstQuestion.getS3AudioUrl(),
        secondQuestion == null ? null : secondQuestion.getQuestionId(),
        secondQuestion == null ? null : secondQuestion.getQuestionText(),
        secondQuestion == null ? null : secondQuestion.getS3AudioUrl(),
        hasNext);
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

  public QuestionDomain fromResultDtoToDomain(
      QuestionResultDto questionResultDto, InterviewDomain interviewDomain) {
    return QuestionDomain.builder()
        .interviewDomain(interviewDomain)
        .questionText(questionResultDto.question().questionText())
        .s3AudioUrl(questionResultDto.question().s3AudioUrl())
        .s3VideoUrl(questionResultDto.question().s3VideoUrl())
        .questionExcerpt(questionResultDto.questionExcerpt())
        .questionIntent(questionResultDto.questionIntent())
        .keyTerms(questionResultDto.keyTerms())
        .build();
  }
}
