package org.richardstallman.dvback.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.global.jwt.domain.response.JwtClaimResponseDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final ObjectMapper objectMapper;
  private final TokenService tokenService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = extractCookie(cookies, "AccessToken");
    String refreshToken = extractCookie(cookies, "RefreshToken");

    if (accessToken == null || jwtUtil.validateToken(accessToken)) {
      if (refreshToken != null || jwtUtil.validateToken(refreshToken)) {
        accessToken = tokenService.renewToken(response, refreshToken);
      } else {
        writeErrorResponse(response);
        return;
      }
    }

    setAuthInSecurityContext(accessToken);
    filterChain.doFilter(request, response);
  }

  private void writeErrorResponse(HttpServletResponse response) throws IOException {
    // Todo: 예외 처리 형식 공부 후 다시 작성
    // ExceptionResponseDTO exceptionResponseDTO = new ExceptionResponseDTO("Invalid token", 403);
    response.setStatus(403);
    response.setContentType("application/json; charset=utf-8");
    // response.getWriter().write(objectMapper.writeValueAsString(exceptionResponseDTO));
  }

  private void setAuthInSecurityContext(String accessToken) {
    JwtClaimResponseDto jwtClaimResponseDto = jwtUtil.extractClaims(accessToken);

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        jwtClaimResponseDto.getUserId(), null, null);

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private String extractCookie(Cookie[] cookies, String cookieName) {
    return Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(cookieName))
        .findFirst()
        .map(Cookie::getValue)
        .orElse(null);
  }
}
