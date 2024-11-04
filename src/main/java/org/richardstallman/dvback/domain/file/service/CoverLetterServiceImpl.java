package org.richardstallman.dvback.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.repository.coverletter.CoverLetterRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoverLetterServiceImpl implements CoverLetterService {

  private final CoverLetterRepository coverLetterRepository;

  @Override
  public CoverLetterDomain createCoverLetter(CoverLetterDomain coverLetterDomain) {
    return coverLetterRepository.save(coverLetterDomain);
  }

  @Override
  public CoverLetterDomain findCoverLetter(Long coverLetterId) {
    return coverLetterRepository.findByCoverLetterId(coverLetterId);
  }
}
