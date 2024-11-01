package org.richardstallman.dvback.global.oauth.handler;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.oauth.OAuth2CustomUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Value("${app.properties.frontendDomain}")
  private String frontendDomain;

  private final JwtUtil jwtUtil;
  private final CookieService cookieService;
  private final TokenService tokenService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    OAuth2CustomUser oAuth2CusomUser = (OAuth2CustomUser) authentication.getPrincipal();

    String accessToken = jwtUtil.generateAccessToken(oAuth2CusomUser.getUserId());
    String refreshToken = jwtUtil.generateRefreshToken(oAuth2CusomUser.getUserId());

    tokenService.saveRefreshToken(refreshToken, oAuth2CusomUser.getUserId());

    response.addHeader(SET_COOKIE,
        cookieService.createCookie(JwtUtil.ACCESS_TOKEN, accessToken).toString());
    response.addHeader(SET_COOKIE,
        cookieService.createCookie(JwtUtil.REFRESH_TOKEN, refreshToken).toString());

    try {
      response.sendRedirect(frontendDomain);
    } catch (Exception e) {
      log.error("Exception occurred after successful OAuth2 login : {}", e.getMessage());
    }
  }

}
