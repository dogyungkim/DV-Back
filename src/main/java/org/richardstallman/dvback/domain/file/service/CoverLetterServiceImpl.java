package org.richardstallman.dvback.domain.file.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;
import org.richardstallman.dvback.domain.file.repository.coverletter.CoverLetterRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoverLetterServiceImpl implements CoverLetterService {

  private final CoverLetterRepository coverLetterRepository;
  private final CoverLetterConverter coverLetterConverter;

  @Override
  public CoverLetterDomain createCoverLetter(CoverLetterDomain coverLetterDomain) {
    return coverLetterRepository.save(coverLetterDomain);
  }

  @Override
  public CoverLetterDomain findCoverLetter(Long coverLetterId) {
    return coverLetterRepository.findByCoverLetterId(coverLetterId);
  }

  @Override
  public List<CoverLetterResponseDto> findCoverLettersByUserId(Long userId) {
    return coverLetterRepository.findCoverLetterListByUserId(userId).stream()
        .map(coverLetterConverter::fromDomainToResponseDto)
        .toList();
  }
}
