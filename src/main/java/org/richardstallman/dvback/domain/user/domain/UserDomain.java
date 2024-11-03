package org.richardstallman.dvback.domain.user.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDomain {

  private Long id;
  private String socialId;
  private String name;
  private String nickname;
  private String profileImage;
}
