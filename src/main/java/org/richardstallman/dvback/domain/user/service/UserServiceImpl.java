package org.richardstallman.dvback.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.oauth.service.CookieService;
import org.richardstallman.dvback.global.oauth.service.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final CookieService cookieService;
  private final TokenService tokenService;

  @Transactional
  public UserResponseDto updateUserInfo(Long userId, UserRequestDto userRequestDto) {
    UserDomain user = findUserById(userId);
    UserEntity userEntity = userConverter.fromDomainToEntity(user);

    UserEntity updatedUser =
        userEntity.updatedUserEntity(
            userRequestDto.nickname(), userRequestDto.birthdate(), userRequestDto.gender());

    userRepository.save(userConverter.fromEntityToDomain(updatedUser));

    return new UserResponseDto(
        updatedUser.getId(),
        updatedUser.getSocialId(),
        updatedUser.getEmail(),
        updatedUser.getName(),
        updatedUser.getNickname(),
        updatedUser.getS3ProfileImageUrl(),
        updatedUser.getLeave(),
        updatedUser.getGender(),
        updatedUser.getBirthdate());
  }

  @Override
  public UserResponseDto getUserInfo(Long userId) {
    log.info("getUserInfo");
    UserDomain userDomain = findUserById(userId);
    return userConverter.fromDomainToDto(userDomain);
  }

  @Override
  public void logout(HttpServletResponse response, String refreshToken) {
    cookieService.deleteCookie(response, "access_token");
    cookieService.deleteCookie(response, "refresh_token");
    tokenService.deleteRefreshTokenByRefreshToken(refreshToken);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  private UserDomain findUserById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
  }
}
