package org.richardstallman.dvback.domain.interview.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.repository.InterviewRepository;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

  private final InterviewRepository interviewRepository;
  private final JobService jobService;
  private final InterviewConverter interviewConverter;

  @Transactional
  public InterviewCreateResponseDto createInterview(
      InterviewCreateRequestDto interviewCreateRequestDto) {

    if (interviewCreateRequestDto.jobId() == null) {
      log.error("Job Id should be not null when creating a new interview");
      throw new ApiException(HttpStatus.BAD_REQUEST, "FAIl");
    }
    JobDomain jobDomain = jobService.findJobById(interviewCreateRequestDto.jobId());

    InterviewDomain interviewDomain =
        interviewConverter.fromDtoToDomainWithStatusInitial(interviewCreateRequestDto, jobDomain);
    if (interviewDomain.getInterviewId() != null) {
      log.error("Interview ID should be null when creating a new interview");
      throw new ApiException(
          HttpStatus.NOT_FOUND, "Interview ID should be null for new interviews");
    }
    InterviewDomain createdInterviewDomain = interviewRepository.save(interviewDomain);
    if (createdInterviewDomain.getInterviewId() == null) {
      log.error("Interview ID should be assigned after saving, but it is null");
      throw new ApiException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Interview ID was not generated properly");
    }

    return interviewConverter.fromDomainToDto(createdInterviewDomain);
  }
}
