package org.richardstallman.dvback.domain.interview.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.repository.InterviewRepository;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

  private final InterviewRepository interviewRepository;
  private final JobService jobService;
  private final InterviewConverter interviewConverter;

  @Transactional
  public InterviewCreateResponseDto createInterview(
      InterviewCreateRequestDto interviewCreateRequestDto) {

    JobDomain jobDomain = jobService.findJobById(interviewCreateRequestDto.jobId());

    InterviewDomain interviewDomain =
        interviewConverter.fromDtoToDomainWithStatusInitial(interviewCreateRequestDto, jobDomain);
    InterviewDomain createdInterviewDomain = interviewRepository.save(interviewDomain);
    return interviewConverter.fromDomainToDto(createdInterviewDomain);
  }
}
