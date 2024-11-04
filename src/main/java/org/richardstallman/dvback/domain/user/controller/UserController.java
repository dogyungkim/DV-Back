package org.richardstallman.dvback.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import org.richardstallman.dvback.global.oauth.service.CookieService;
import org.richardstallman.dvback.global.oauth.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

  private final TokenService tokenService;
  private final CookieService cookieService;
  private final RefreshTokenRepository refreshTokenRepository;

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    cookieService.createExpiredCookie(response, JwtUtil.ACCESS_TOKEN);
    cookieService.createExpiredCookie(response, JwtUtil.REFRESH_TOKEN);

    String refreshToken =
        tokenService.getTokenFromCookies(request.getCookies(), JwtUtil.REFRESH_TOKEN);
    if (refreshToken != null) {
      refreshTokenRepository.deleteById(refreshToken);
    }

    return ResponseEntity.ok("Logged out successfully");
  }
}
