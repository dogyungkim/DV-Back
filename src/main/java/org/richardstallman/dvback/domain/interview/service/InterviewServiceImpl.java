package org.richardstallman.dvback.domain.interview.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.file.service.CoverLetterService;
import org.richardstallman.dvback.domain.file.service.FileService;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.repository.InterviewRepository;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.service.UserService;
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
  private final UserService userService;

  public static final String ERROR_INTERVIEW_ID_NOT_NULL =
      "Interview ID should be null for new interviews";
  public static final String ERROR_INTERVIEW_ID_NULL_AFTER_SAVE =
      "Interview ID was not generated properly";

  @Transactional
  public InterviewCreateResponseDto createInterview(
      InterviewCreateRequestDto interviewCreateRequestDto, Long userId) {

    JobDomain jobDomain = jobService.findJobById(interviewCreateRequestDto.jobId());

    InterviewDomain interviewDomain =
        initializeInterviewDomain(interviewCreateRequestDto, jobDomain, userId);

    validateInterviewIdNull(interviewDomain);

    InterviewDomain createdInterviewDomain = interviewRepository.save(interviewDomain);
    validateInterviewIdAssigned(createdInterviewDomain);

    List<FileResponseDto> fileResponseDtos = new ArrayList<>();
    fileResponseDtos.add(
        coverLetterConverter.fromDomainToResponseDto(createdInterviewDomain.getCoverLetter()));

    return interviewConverter.fromDomainToDto(createdInterviewDomain, fileResponseDtos);
  }

  private InterviewDomain initializeInterviewDomain(
      InterviewCreateRequestDto interviewCreateRequestDto, JobDomain jobDomain, Long userId) {
    if (interviewCreateRequestDto.interviewMode() == InterviewMode.REAL) {
      String fileName =
          fileService.getFileName(getCoverLetterUrl(interviewCreateRequestDto.files()));
      UserResponseDto userResponseDto = userService.getUserInfo(userId);
      CoverLetterDomain coverLetterDomain =
          createCoverLetter(interviewCreateRequestDto, fileName, userResponseDto);
      return interviewConverter.fromDtoToDomainWithStatusInitialWithModeReal(
          interviewCreateRequestDto, jobDomain, coverLetterDomain, userResponseDto);
    } else {
      UserResponseDto userResponseDto = userService.getUserInfo(userId);
      return interviewConverter.fromDtoToDomainWithStatusInitial(
          interviewCreateRequestDto, jobDomain, userResponseDto);
    }
  }

  private String getCoverLetterUrl(List<FileRequestDto> files) {
    CoverLetterRequestDto coverLetterRequestDto =
        (CoverLetterRequestDto)
            files.stream()
                .filter(item -> item.getType().equals(FileType.COVER_LETTER))
                .findFirst()
                .orElseThrow(
                    () ->
                        new ApiException(
                            HttpStatus.NOT_FOUND, "There is no cover letter in files."));
    return coverLetterRequestDto.getFilePath();
  }

  private CoverLetterDomain createCoverLetter(
      InterviewCreateRequestDto requestDto, String fileName, UserResponseDto userResponseDto) {
    return coverLetterService.createCoverLetter(
        coverLetterConverter.fromUrlToDomain(
            getCoverLetterUrl(requestDto.files()), userResponseDto, fileName));
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
