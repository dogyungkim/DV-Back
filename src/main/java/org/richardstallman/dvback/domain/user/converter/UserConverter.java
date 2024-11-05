package org.richardstallman.dvback.domain.user.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.job.converter.JobConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.global.oauth.KakaoUserInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

  private final JobConverter jobConverter;

  public UserEntity fromDomainToEntity(UserDomain userDomain) {
    return new UserEntity(
        userDomain.getId(),
        userDomain.getSocialId(),
        userDomain.getEmail(),
        userDomain.getName(),
        userDomain.getNickname(),
        userDomain.getS3ProfileImageUrl(),
        userDomain.getLeave(),
        userDomain.getGender(),
        userDomain.getBirthdate());
  }

  public UserDomain fromEntityToDomain(UserEntity userEntity) {
    return UserDomain.builder()
        .id(userEntity.getId())
        .socialId(userEntity.getSocialId())
        .email(userEntity.getEmail())
        .name(userEntity.getName())
        .nickname(userEntity.getNickname())
        .s3ProfileImageUrl(userEntity.getS3ProfileImageUrl())
        .leave(userEntity.getLeave())
        .gender(userEntity.getGender())
        .birthdate(userEntity.getBirthdate())
        .build();
  }

  public UserEntity kakaoInfoToUserEntity(KakaoUserInfo kakaoUserInfo) {
    return new UserEntity(
        kakaoUserInfo.getId(),
        kakaoUserInfo.getEmail(),
        kakaoUserInfo.getNickname(),
        kakaoUserInfo.getNickname(),
        kakaoUserInfo.getProfileImage(),
        null,
        null
    );
  }
}
