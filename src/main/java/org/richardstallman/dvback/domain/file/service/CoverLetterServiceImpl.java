package org.richardstallman.dvback.domain.file.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;
import org.richardstallman.dvback.domain.file.repository.coverletter.CoverLetterRepository;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoverLetterServiceImpl implements CoverLetterService {

  private final CoverLetterRepository coverLetterRepository;
  private final CoverLetterConverter coverLetterConverter;
  private final InterviewService interviewService;

  public CoverLetterServiceImpl(
      CoverLetterRepository coverLetterRepository,
      CoverLetterConverter coverLetterConverter,
      @Lazy InterviewService interviewService) {
    this.coverLetterRepository = coverLetterRepository;
    this.coverLetterConverter = coverLetterConverter;
    this.interviewService = interviewService;
  }

  @Override
  public CoverLetterDomain createCoverLetter(CoverLetterDomain coverLetterDomain) {
    return coverLetterRepository.save(coverLetterDomain);
  }

  @Override
  public CoverLetterDomain findCoverLetter(Long coverLetterId) {
    return coverLetterRepository.findByCoverLetterId(coverLetterId);
  }

  @Override
  public CoverLetterDomain findCoverLetterByInterviewId(Long interviewId) {
    return interviewService.getInterviewById(interviewId).getCoverLetter();
  }

  @Override
  public List<CoverLetterResponseDto> findCoverLettersByUserId(Long userId) {
    return coverLetterRepository.findCoverLetterListByUserId(userId).stream()
        .map(coverLetterConverter::fromDomainToResponseDto)
        .toList();
  }
}
