package org.richardstallman.dvback.domain.file.service;

import java.util.List;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;

public interface CoverLetterService {

  CoverLetterDomain createCoverLetter(CoverLetterDomain coverLetterDomain);

  CoverLetterDomain findCoverLetter(Long coverLetterId);

  CoverLetterDomain findCoverLetterByInterviewId(Long interviewId);

  List<CoverLetterResponseDto> findCoverLettersByUserId(Long userId);
}
