package org.richardstallman.dvback.domain.interview.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.richardstallman.dvback.domain.job.converter.JobConverter;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewConverter {

  private final JobConverter jobConverter;

  public InterviewEntity fromDomainToEntity(InterviewDomain interviewDomain) {
    return new InterviewEntity(
        interviewDomain.getInterviewId(),
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        jobConverter.toEntity(interviewDomain.getJob()));
  }

  public InterviewEntity fromDomainToEntityWhenCreate(InterviewDomain interviewDomain) {
    return new InterviewEntity(
        interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(),
        interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(),
        jobConverter.toEntity(interviewDomain.getJob()));
  }

  public InterviewDomain fromEntityToDomain(InterviewEntity interviewEntity) {
    return InterviewDomain.builder()
        .interviewId(interviewEntity.getInterviewId())
        .interviewStatus(interviewEntity.getInterviewStatus())
        .interviewType(interviewEntity.getInterviewType())
        .interviewMethod(interviewEntity.getInterviewMethod())
        .interviewMode(interviewEntity.getInterviewMode())
        .job(jobConverter.toDomain(interviewEntity.getJob()))
        .build();
  }

  public InterviewDomain fromDtoToDomainWithStatusInitial(
      InterviewCreateRequestDto interviewCreateRequestDto, JobDomain job) {
    return InterviewDomain.builder()
        .interviewStatus(InterviewStatus.INITIAL)
        .interviewType(interviewCreateRequestDto.interviewType())
        .interviewMethod(interviewCreateRequestDto.interviewMethod())
        .interviewMode(interviewCreateRequestDto.interviewMode())
        .job(job)
        .build();
  }

  public InterviewCreateResponseDto fromDomainToDto(InterviewDomain interviewDomain) {
    return new InterviewCreateResponseDto(
        interviewDomain.getInterviewId(), interviewDomain.getInterviewStatus(),
        interviewDomain.getInterviewType(), interviewDomain.getInterviewMethod(),
        interviewDomain.getInterviewMode(), interviewDomain.getJob());
  }
}
