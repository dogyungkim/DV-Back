package org.richardstallman.dvback.global.oauth.service;

import static org.richardstallman.dvback.global.jwt.JwtUtil.ACCESS_TOKEN;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.jwt.refreshtoken.entity.RefreshTokenEntity;
import org.richardstallman.dvback.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final CookieService cookieService;
  private final UserConverter userConverter;

  public String renewToken(HttpServletResponse httpServletResponse, String refreshToken) {
    String newAccessToken = createAccessToken(refreshToken);
    ResponseCookie accessCookie = cookieService.createCookie(ACCESS_TOKEN, newAccessToken);
    httpServletResponse.addHeader(SET_COOKIE, accessCookie.toString());
    return newAccessToken;
  }

  public String createAccessToken(String refreshToken) {
    RefreshTokenEntity storedRefreshToken =
        refreshTokenRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(() -> new EntityNotFoundException("Refresh Token not found."));
    UserDomain userDomain =
        userRepository
            .findById(storedRefreshToken.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User not found."));

    UserEntity userEntity = userConverter.toEntity(userDomain);
    return jwtUtil.generateAccessToken(userEntity.getId());
  }

  public void saveRefreshToken(String refreshToken, Long userId) {
    log.info("saveRefreshToken - refreshToken : {}", refreshToken);
    log.info("saveRefreshToken - userId : {}", userId);
    deleteRefreshTokenByRefreshToken(refreshToken);
    refreshTokenRepository.save(new RefreshTokenEntity(refreshToken, userId));
  }

  public void deleteRefreshTokenByUserId(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }

  public void deleteRefreshTokenByRefreshToken(String refreshToken) {
    log.info("deleteRefreshTokenByRefreshToken - refreshToken : {}", refreshToken);
    refreshTokenRepository.deleteById(refreshToken);
  }

  public String getTokenFromCookies(Cookie[] cookies, String tokenName) {
    if (cookies == null) return null;
    for (Cookie cookie : cookies) {
      if (tokenName.equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
