package org.richardstallman.dvback.domain.user.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

  public UserEntity toEntity(UserDomain userDomain) {
    return new UserEntity(
        userDomain.getId(),
        userDomain.getSocialId(),
        userDomain.getName(),
        userDomain.getNickname(),
        userDomain.getProfileImage());
  }

  public UserDomain toDomain(UserEntity userEntity) {
    return UserDomain.builder()
        .id(userEntity.getId())
        .socialId(userEntity.getSocialId())
        .name(userEntity.getName())
        .nickname(userEntity.getNickname())
        .profileImage(userEntity.getProfileImage())
        .build();
  }
}
