package org.richardstallman.dvback.domain.interview.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.richardstallman.dvback.domain.job.converter.JobConverter;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewConverter {

  private final JobConverter jobConverter;
  private final CoverLetterConverter coverLetterConverter;

  public InterviewEntity fromDomainToEntity(InterviewDomain interviewDomain) {
    return new InterviewEntity(
        interviewDomain.getUserId(),
        interviewDomain.getInterviewId(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        jobConverter.toEntity(interviewDomain.getJob()),
        coverLetterConverter.fromDomainToEntity(interviewDomain.getCoverLetter()));
  }

  public InterviewEntity fromDomainToEntityWhenCreate(InterviewDomain interviewDomain) {
    return new InterviewEntity(
        interviewDomain.getUserId(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        jobConverter.toEntity(interviewDomain.getJob()),
        coverLetterConverter.fromDomainToEntity(interviewDomain.getCoverLetter()));
  }

  public InterviewDomain fromEntityToDomain(InterviewEntity interviewEntity) {
    return InterviewDomain.builder()
        .userId(interviewEntity.getUserId())
        .interviewId(interviewEntity.getInterviewId())
        .interviewStatus(interviewEntity.getInterviewStatus())
        .interviewType(interviewEntity.getInterviewType())
        .interviewMethod(interviewEntity.getInterviewMethod())
        .interviewMode(interviewEntity.getInterviewMode())
        .job(jobConverter.toDomain(interviewEntity.getJob()))
        .coverLetter(coverLetterConverter.fromEntityToDomain(interviewEntity.getCoverLetter()))
        .build();
  }

  public InterviewDomain fromDtoToDomainWithStatusInitial(
      InterviewCreateRequestDto interviewCreateRequestDto, JobDomain job) {
    return InterviewDomain.builder()
        .userId(interviewCreateRequestDto.userId())
        .interviewStatus(InterviewStatus.INITIAL)
        .interviewType(interviewCreateRequestDto.interviewType())
        .interviewMethod(interviewCreateRequestDto.interviewMethod())
        .interviewMode(interviewCreateRequestDto.interviewMode())
        .job(job)
        .build();
  }

  public InterviewDomain fromDtoToDomainWithStatusInitialWithModeReal(
      InterviewCreateRequestDto interviewCreateRequestDto,
      JobDomain job,
      CoverLetterDomain coverLetterDomain) {
    return InterviewDomain.builder()
        .userId(interviewCreateRequestDto.userId())
        .interviewStatus(InterviewStatus.INITIAL)
        .interviewType(interviewCreateRequestDto.interviewType())
        .interviewMethod(interviewCreateRequestDto.interviewMethod())
        .interviewMode(interviewCreateRequestDto.interviewMode())
        .job(job)
        .coverLetter(coverLetterDomain)
        .build();
  }

  public InterviewCreateResponseDto fromDomainToDto(InterviewDomain interviewDomain) {
    return new InterviewCreateResponseDto(
        interviewDomain.getInterviewId(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        interviewDomain.getJob(),
        interviewDomain.getCoverLetter());
  }

  public InterviewDomain fromInterviewInitialQuestionRequestDtoToDomain(
      QuestionInitialRequestDto questionInitialRequestDto,
      JobDomain jobDomain,
      CoverLetterDomain coverLetterDomain) {
    return InterviewDomain.builder()
        .interviewId(questionInitialRequestDto.interviewId())
        .interviewStatus(InterviewStatus.IN_PROGRESS)
        .interviewType(questionInitialRequestDto.interviewType())
        .interviewMethod(questionInitialRequestDto.interviewMethod())
        .interviewMode(questionInitialRequestDto.interviewMode())
        .job(jobDomain)
        .coverLetter(coverLetterDomain)
        .build();
  }
}
