package org.richardstallman.dvback.domain.user.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.domain.response.UserLoginResponseDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.global.oauth.KakaoUserInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

  public UserEntity fromDomainToEntity(UserDomain userDomain) {
    return new UserEntity(
        userDomain.getId(),
        userDomain.getSocialId(),
        userDomain.getEmail(),
        userDomain.getUsername(),
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
    return new UserEntity(kakaoUserInfo.getId(), kakaoUserInfo.getEmail());
  }

  public UserResponseDto fromDomainToDto(UserDomain userDomain) {
    return new UserResponseDto(
        userDomain.getId(),
        userDomain.getSocialId(),
        userDomain.getEmail(),
        userDomain.getUsername(),
        userDomain.getName(),
        userDomain.getNickname(),
        userDomain.getS3ProfileImageUrl(),
        userDomain.getLeave(),
        userDomain.getGender(),
        userDomain.getBirthdate());
  }

  public UserDomain fromResponseDtoToDomain(UserResponseDto userResponseDto) {
    return UserDomain.builder()
        .id(userResponseDto.userId())
        .socialId(userResponseDto.socialId())
        .email(userResponseDto.email())
        .name(userResponseDto.name())
        .nickname(userResponseDto.nickname())
        .s3ProfileImageUrl(userResponseDto.s3ProfileImageUrl())
        .leave(userResponseDto.leave())
        .gender(userResponseDto.gender())
        .birthdate(userResponseDto.birthdate())
        .build();
  }

  public UserLoginResponseDto fromResponseDtoToLoginResponseDto(
      UserResponseDto userResponseDto, String type) {
    return new UserLoginResponseDto(
        type,
        userResponseDto.userId(),
        userResponseDto.socialId(),
        userResponseDto.email(),
        userResponseDto.username(),
        userResponseDto.name(),
        userResponseDto.nickname(),
        userResponseDto.s3ProfileImageUrl(),
        userResponseDto.leave(),
        userResponseDto.gender(),
        userResponseDto.birthdate());
  }

  public UserLoginResponseDto forSignUp(UserResponseDto userResponseDto, String type) {
    return new UserLoginResponseDto(
        type, userResponseDto.userId(), null, null, null, null, null, null, null, null, null);
  }
}
