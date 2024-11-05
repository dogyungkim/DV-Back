package org.richardstallman.dvback.domain.user.domain;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.domain.job.domain.JobDomain;

@Getter
@Builder
public class UserDomain {

  private Long id;
  private String socialId;
  private String email;
  private String name;
  private String nickname;
  private String s3ProfileImageUrl;
  private Boolean leave;
  private CommonConstants.Gender gender;
  private Date birthdate;
  private JobDomain job;
}
