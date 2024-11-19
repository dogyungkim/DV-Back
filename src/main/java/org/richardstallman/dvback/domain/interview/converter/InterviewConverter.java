package org.richardstallman.dvback.domain.interview.converter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewEvaluationResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewQuestionResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.richardstallman.dvback.domain.job.converter.JobConverter;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketResponseDto;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewConverter {

  private final JobConverter jobConverter;
  private final CoverLetterConverter coverLetterConverter;
  private final UserConverter userConverter;

  public InterviewEntity fromDomainToEntity(InterviewDomain interviewDomain) {
    return new InterviewEntity(
        interviewDomain.getInterviewId(),
        userConverter.fromDomainToEntity(interviewDomain.getUserDomain()),
        interviewDomain.getInterviewTitle(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        interviewDomain.getQuestionCount(),
        jobConverter.toEntity(interviewDomain.getJob()),
        interviewDomain.getInterviewMode() == InterviewMode.REAL
            ? coverLetterConverter.fromDomainToEntity(interviewDomain.getCoverLetter())
            : null);
  }

  public InterviewEntity fromDomainToEntityWhenCreate(InterviewDomain interviewDomain) {
    return new InterviewEntity(
        userConverter.fromDomainToEntity(interviewDomain.getUserDomain()),
        interviewDomain.getInterviewTitle(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        interviewDomain.getQuestionCount(),
        jobConverter.toEntity(interviewDomain.getJob()),
        interviewDomain.getInterviewMode() == InterviewMode.REAL
            ? coverLetterConverter.fromDomainToEntity(interviewDomain.getCoverLetter())
            : null);
  }

  public InterviewDomain fromEntityToDomain(InterviewEntity interviewEntity) {
    return InterviewDomain.builder()
        .userDomain(userConverter.fromEntityToDomain(interviewEntity.getUser()))
        .interviewTitle(interviewEntity.getInterviewTitle())
        .interviewId(interviewEntity.getInterviewId())
        .interviewStatus(interviewEntity.getInterviewStatus())
        .interviewType(interviewEntity.getInterviewType())
        .interviewMethod(interviewEntity.getInterviewMethod())
        .interviewMode(interviewEntity.getInterviewMode())
        .questionCount(interviewEntity.getQuestionCount())
        .job(jobConverter.toDomain(interviewEntity.getJob()))
        .coverLetter(
            interviewEntity.getInterviewMode() == InterviewMode.REAL
                ? coverLetterConverter.fromEntityToDomain(interviewEntity.getCoverLetter())
                : null)
        .build();
  }

  public InterviewDomain fromDtoToDomainWithStatusInitial(
      InterviewCreateRequestDto interviewCreateRequestDto,
      JobDomain job,
      UserResponseDto userResponseDto) {
    return InterviewDomain.builder()
        .userDomain(userConverter.fromResponseDtoToDomain(userResponseDto))
        .interviewTitle(interviewCreateRequestDto.interviewTitle())
        .interviewStatus(InterviewStatus.INITIAL)
        .interviewType(interviewCreateRequestDto.interviewType())
        .interviewMethod(interviewCreateRequestDto.interviewMethod())
        .interviewMode(interviewCreateRequestDto.interviewMode())
        .questionCount(
            interviewCreateRequestDto.interviewMode() == InterviewMode.GENERAL
                ? 3
                : interviewCreateRequestDto.questionCount())
        .job(job)
        .build();
  }

  public InterviewDomain fromDtoToDomainWithStatusInitialWithModeReal(
      InterviewCreateRequestDto interviewCreateRequestDto,
      JobDomain job,
      CoverLetterDomain coverLetterDomain,
      UserResponseDto userResponseDto) {
    return InterviewDomain.builder()
        .userDomain(userConverter.fromResponseDtoToDomain(userResponseDto))
        .interviewTitle(interviewCreateRequestDto.interviewTitle())
        .interviewStatus(InterviewStatus.INITIAL)
        .interviewType(interviewCreateRequestDto.interviewType())
        .interviewMethod(interviewCreateRequestDto.interviewMethod())
        .interviewMode(interviewCreateRequestDto.interviewMode())
        .questionCount(interviewCreateRequestDto.questionCount())
        .job(job)
        .coverLetter(coverLetterDomain)
        .build();
  }

  public InterviewCreateResponseDto fromDomainToCreateResponseDto(
      InterviewDomain interviewDomain,
      List<FileResponseDto> fileResponseDtos,
      TicketResponseDto ticketResponseDto) {
    return new InterviewCreateResponseDto(
        interviewDomain.getInterviewId(),
        interviewDomain.getInterviewTitle(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        interviewDomain.getQuestionCount(),
        interviewDomain.getJob(),
        fileResponseDtos,
        ticketResponseDto);
  }

  public InterviewResponseDto fromDomainToResponseDto(
      InterviewDomain interviewDomain, List<FileResponseDto> fileResponseDtos) {
    return new InterviewResponseDto(
        interviewDomain.getInterviewId(),
        interviewDomain.getInterviewTitle(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        interviewDomain.getQuestionCount(),
        interviewDomain.getJob(),
        fileResponseDtos);
  }

  public InterviewQuestionResponseDto fromDomainToQuestionResponseDto(
      InterviewDomain interviewDomain) {
    return new InterviewQuestionResponseDto(
        interviewDomain.getInterviewId(),
        interviewDomain.getInterviewTitle(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        interviewDomain.getQuestionCount(),
        interviewDomain.getJob());
  }

  public InterviewDomain fromInterviewInitialQuestionRequestDtoToDomain(
      QuestionInitialRequestDto questionInitialRequestDto,
      JobDomain jobDomain,
      CoverLetterDomain coverLetterDomain,
      UserDomain userDomain) {
    return InterviewDomain.builder()
        .interviewId(questionInitialRequestDto.interviewId())
        .userDomain(userDomain)
        .interviewTitle(questionInitialRequestDto.interviewTitle())
        .interviewStatus(InterviewStatus.IN_PROGRESS)
        .interviewType(questionInitialRequestDto.interviewType())
        .interviewMethod(questionInitialRequestDto.interviewMethod())
        .interviewMode(questionInitialRequestDto.interviewMode())
        .questionCount(questionInitialRequestDto.questionCount())
        .job(jobDomain)
        .coverLetter(coverLetterDomain)
        .build();
  }

  public InterviewEvaluationResponseDto fromDomainToEvaluationResponseDto(
      InterviewDomain interviewDomain) {
    return new InterviewEvaluationResponseDto(
        interviewDomain.getInterviewId(), interviewDomain.getInterviewTitle());
  }
}
