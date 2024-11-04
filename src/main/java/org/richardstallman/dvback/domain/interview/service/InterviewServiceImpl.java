package org.richardstallman.dvback.domain.interview.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.service.CoverLetterService;
import org.richardstallman.dvback.domain.file.service.FileService;
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
  private final FileService fileService;
  private final CoverLetterConverter coverLetterConverter;
  private final CoverLetterService coverLetterService;

  public static final String ERROR_INTERVIEW_ID_NOT_NULL =
      "Interview ID should be null for new interviews";
  public static final String ERROR_INTERVIEW_ID_NULL_AFTER_SAVE =
      "Interview ID was not generated properly";

  @Transactional
  public InterviewCreateResponseDto createInterview(
      InterviewCreateRequestDto interviewCreateRequestDto) {

    JobDomain jobDomain = jobService.findJobById(interviewCreateRequestDto.jobId());

    InterviewDomain interviewDomain =
        initializeInterviewDomain(interviewCreateRequestDto, jobDomain);

    validateInterviewIdNull(interviewDomain);

    InterviewDomain createdInterviewDomain = interviewRepository.save(interviewDomain);
    validateInterviewIdAssigned(createdInterviewDomain);

    return interviewConverter.fromDomainToDto(createdInterviewDomain);
  }

  private InterviewDomain initializeInterviewDomain(
      InterviewCreateRequestDto interviewCreateRequestDto, JobDomain jobDomain) {
    if (interviewCreateRequestDto.interviewMode() == InterviewMode.REAL) {
      String fileName = fileService.getFileName(interviewCreateRequestDto.coverLetterUrl());
      CoverLetterDomain coverLetterDomain = createCoverLetter(interviewCreateRequestDto, fileName);
      return interviewConverter.fromDtoToDomainWithStatusInitialWithModeReal(
          interviewCreateRequestDto, jobDomain, coverLetterDomain);
    } else {
      return interviewConverter.fromDtoToDomainWithStatusInitial(
          interviewCreateRequestDto, jobDomain);
    }
  }

  private CoverLetterDomain createCoverLetter(
      InterviewCreateRequestDto requestDto, String fileName) {
    return coverLetterService.createCoverLetter(
        coverLetterConverter.fromUrlToDomain(
            requestDto.coverLetterUrl(), requestDto.userId(), fileName));
  }

  private void validateInterviewIdNull(InterviewDomain interviewDomain) {
    if (interviewDomain.getInterviewId() != null) {
      log.error(ERROR_INTERVIEW_ID_NOT_NULL);
      throw new ApiException(HttpStatus.NOT_FOUND, ERROR_INTERVIEW_ID_NOT_NULL);
    }
  }

  private void validateInterviewIdAssigned(InterviewDomain interviewDomain) {
    if (interviewDomain.getInterviewId() == null) {
      log.error(ERROR_INTERVIEW_ID_NULL_AFTER_SAVE);
      throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERVIEW_ID_NULL_AFTER_SAVE);
    }
  }
}
