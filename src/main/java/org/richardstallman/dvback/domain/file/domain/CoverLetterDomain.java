package org.richardstallman.dvback.domain.file.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CoverLetterDomain {

  private final Long coverLetterId;
  private final Long userId;
  private String fileName;
  private String s3FileUrl;
}
