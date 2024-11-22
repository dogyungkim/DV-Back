package org.richardstallman.dvback.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserLoginResponseDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.service.UserService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.oauth.service.TokenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

  private final TokenService tokenService;
  private final UserService userService;
  private final UserConverter userConverter;
  private final JwtUtil jwtUtil;

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken =
        tokenService.getTokenFromCookies(request.getCookies(), JwtUtil.REFRESH_TOKEN);
    userService.logout(response, refreshToken);

    return ResponseEntity.ok("Logged out successfully");
  }

  @PostMapping("/info")
  public ResponseEntity<DvApiResponse<UserResponseDto>> updateUserInfo(
      @AuthenticationPrincipal Long userId,
      @RequestPart("userInfo") @Valid final UserRequestDto userRequestDto,
      @RequestPart("profileImage") @NotNull MultipartFile profileImage)
      throws IOException {
    final UserResponseDto userResponseDto =
        userService.updateUserInfo(userId, userRequestDto, profileImage);
    return ResponseEntity.ok(DvApiResponse.of(userResponseDto));
  }

  @GetMapping("/info")
  public ResponseEntity<DvApiResponse<UserResponseDto>> getUserInfo(
      @AuthenticationPrincipal Long userId) {
    final UserResponseDto userResponseDto = userService.getUserInfo(userId);
    return ResponseEntity.ok(DvApiResponse.of(userResponseDto));
  }

  @GetMapping("/login")
  public ResponseEntity<DvApiResponse<UserLoginResponseDto>> login(
      @AuthenticationPrincipal Long userId) {
    final UserResponseDto userResponseDto = userService.getUserInfo(userId);
    if (userResponseDto.gender() == null) {
      return ResponseEntity.ok(
          DvApiResponse.of(userConverter.forSignUp(userResponseDto, "signup")));
    }
    return ResponseEntity.ok(
        DvApiResponse.of(
            userConverter.fromResponseDtoToLoginResponseDto(userResponseDto, "login")));
  }

  @GetMapping("/authenticated")
  public ResponseEntity<DvApiResponse<Boolean>> isAuthenticated(HttpServletRequest request) {
    String accessToken =
        tokenService.getTokenFromCookies(request.getCookies(), JwtUtil.ACCESS_TOKEN);

    if (accessToken == null || accessToken.isEmpty()) {
      return ResponseEntity.ok(DvApiResponse.of(false));
    }

    if (jwtUtil.validateToken(accessToken)) {
      return ResponseEntity.ok(DvApiResponse.of(false));
    }

    return ResponseEntity.ok(DvApiResponse.of(true));
  }

  @GetMapping("/profile-image")
  public ResponseEntity<DvApiResponse<String>> getProfileImage(
      @AuthenticationPrincipal Long userId) {
    String profileImageUrl = userService.getProfileImage(userId);
    return ResponseEntity.ok(DvApiResponse.of(profileImageUrl));
  }

  @GetMapping("/validate-username")
  public ResponseEntity<DvApiResponse<Boolean>> validateUsername(
      @RequestParam("username") String username) {

    if (userService.existsByUsername(username)) {
      return ResponseEntity.ok(DvApiResponse.of(true));
    }

    return ResponseEntity.ok(DvApiResponse.of(false));
  }
}
