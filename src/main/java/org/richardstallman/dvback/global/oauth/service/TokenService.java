package org.richardstallman.dvback.global.oauth.service;

import static org.richardstallman.dvback.global.jwt.JwtUtil.ACCESS_TOKEN;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.jwt.refreshtoken.converter.RefreshTokenConverter;
import org.richardstallman.dvback.global.jwt.refreshtoken.domain.RefreshTokenDomain;
import org.richardstallman.dvback.global.jwt.refreshtoken.entity.RefreshTokenEntity;
import org.richardstallman.dvback.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final CookieService cookieService;
  private final UserConverter userConverter;
  private final RefreshTokenConverter refreshTokenConverter;

  public String renewToken(HttpServletResponse httpServletResponse, String refreshToken) {
    String newAccessToken = createAccessToken(refreshToken);
    ResponseCookie accessCookie = cookieService.createCookie(ACCESS_TOKEN, newAccessToken);
    httpServletResponse.addHeader(SET_COOKIE, accessCookie.toString());
    return newAccessToken;
  }

  public String createAccessToken(String refreshToken) {
    RefreshTokenDomain storedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new EntityNotFoundException("Refresh Token not found."));
    UserDomain userDomain = userRepository.findById(storedRefreshToken.getUserId())
        .orElseThrow(() -> new EntityNotFoundException("User not found."));

    UserEntity userEntity = userConverter.toEntity(userDomain);
    return jwtUtil.generateAccessToken(userEntity.getId());
  }

  public void saveRefreshToken(String refreshToken, Long userId) {
    RefreshTokenDomain refreshTokenDomain = refreshTokenConverter.toDomain(
        new RefreshTokenEntity(refreshToken, userId));
    refreshTokenRepository.save(refreshTokenDomain);
  }

  public void deleteRefreshToken(String refreshToken) {
    refreshTokenRepository.deleteByToken(refreshToken);
  }
}
