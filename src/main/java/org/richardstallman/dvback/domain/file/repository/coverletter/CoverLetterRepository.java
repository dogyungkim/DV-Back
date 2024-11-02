package org.richardstallman.dvback.domain.file.repository.coverletter;

import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;

public interface CoverLetterRepository {

  CoverLetterDomain save(CoverLetterDomain coverLetterDomain);
}
