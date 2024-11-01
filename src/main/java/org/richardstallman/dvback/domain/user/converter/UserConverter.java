package org.richardstallman.dvback.domain.user.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

  public UserEntity toEntity(UserDomain userDomain) {
    return UserEntity.of(
        userDomain.getSocialId(),
        userDomain.getEmail(),
        userDomain.getName(),
        userDomain.getNickname(),
        userDomain.getProfileImage()
    );
  }

  public UserDomain toDomain(UserEntity userEntity) {
    return UserDomain.builder()
        .id(userEntity.getId())
        .socialId(userEntity.getSocialId())
        .email(userEntity.getEmail())
        .name(userEntity.getName())
        .nickname(userEntity.getNickname())
        .profileImage(userEntity.getProfileImage())
        .build();
  }
}
