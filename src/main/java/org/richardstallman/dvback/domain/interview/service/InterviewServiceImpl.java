package org.richardstallman.dvback.domain.interview.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.repository.InterviewRepository;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

  private final InterviewRepository interviewRepository;
  private final JobService jobService;

  public InterviewCreateResponseDto createInterview(
      InterviewCreateRequestDto interviewCreateRequestDto) {

    JobDomain jobDomain = jobService.findJobById(interviewCreateRequestDto.getJobId());

    InterviewDomain interviewDomain = InterviewDomain.create(interviewCreateRequestDto, jobDomain);
    InterviewDomain createdInterviewDomain = interviewRepository.save(interviewDomain);
    return InterviewCreateResponseDto.builder()
        .interviewId(createdInterviewDomain.getInterviewId())
        .interviewStatus(createdInterviewDomain.getInterviewStatus())
        .interviewType(createdInterviewDomain.getInterviewType())
        .interviewMethod(createdInterviewDomain.getInterviewMethod())
        .interviewMode(createdInterviewDomain.getInterviewMode())
        .build();
  }
}
