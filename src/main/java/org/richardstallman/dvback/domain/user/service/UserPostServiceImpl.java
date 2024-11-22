package org.richardstallman.dvback.domain.user.service;

import static org.richardstallman.dvback.common.constant.CommonConstants.FileType.PROFILE_IMAGE;
import static org.richardstallman.dvback.global.util.TimeUtil.generateExpirationDateTime;
import static org.richardstallman.dvback.global.util.TimeUtil.getCurrentDateTime;

import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.domain.coupon.converter.CouponConverter;
import org.richardstallman.dvback.domain.coupon.repository.CouponRepository;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.config.coupons.CouponsConfig;
import org.richardstallman.dvback.global.oauth.service.CookieService;
import org.richardstallman.dvback.global.oauth.service.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPostServiceImpl implements UserPostService {

  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final CookieService cookieService;
  private final TokenService tokenService;
  private final CouponRepository couponRepository;
  private final CouponsConfig couponsConfig;
  private final CouponConverter couponConverter;
  private final S3Service s3Service;

  @Transactional
  public UserResponseDto updateUserInfo(
      Long userId, UserRequestDto userRequestDto, MultipartFile profileImage) throws IOException {
    UserDomain user = findUserById(userId);

    validateUsername(userRequestDto.username());
    validateNickname(userRequestDto.nickname());

    UserEntity userEntity = userConverter.fromDomainToEntity(user);
    String profileImageUrl = s3Service.uploadImageToS3(profileImage, PROFILE_IMAGE, userId);

    UserEntity updatedUser =
        userEntity.updatedUserEntity(
            userRequestDto.username(),
            userRequestDto.name(),
            userRequestDto.nickname(),
            profileImageUrl,
            userRequestDto.birthdate(),
            userRequestDto.gender());

    user = userRepository.save(userConverter.fromEntityToDomain(updatedUser));

    LocalDateTime now = getCurrentDateTime();
    LocalDateTime expireAt = generateExpirationDateTime(now);
    for (CouponsConfig.WelcomeCoupons welcomeCoupons : couponsConfig.getWelcomeCoupons()) {
      couponRepository.save(
          couponConverter.fromWelcomeCouponToDomain(welcomeCoupons, user, now, expireAt));
    }

    return new UserResponseDto(
        updatedUser.getId(),
        updatedUser.getSocialId(),
        updatedUser.getEmail(),
        updatedUser.getUsername(),
        updatedUser.getName(),
        updatedUser.getNickname(),
        updatedUser.getS3ProfileImageUrl(),
        updatedUser.getLeave(),
        updatedUser.getGender(),
        updatedUser.getBirthdate());
  }

  private UserDomain findUserById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
  }

  private void validateUsername(String username) {
    if (!username.matches("^[a-zA-Z0-9]+$")) {
      throw new IllegalArgumentException("Username must contain only letters and numbers.");
    }

    boolean usernameExists = userRepository.existsByUsername(username);
    if (usernameExists) {
      throw new IllegalArgumentException("Username is already taken.");
    }
  }

  private void validateNickname(String nickname) {
    if (!nickname.matches("^[a-zA-Z0-9]+$")) {
      throw new IllegalArgumentException("Nickname must contain only letters and numbers.");
    }
  }
}
