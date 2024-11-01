package org.richardstallman.dvback.global.config.security;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.global.jwt.JwtAuthorizationFilter;
import org.richardstallman.dvback.global.oauth.handler.OAuth2FailureHandler;
import org.richardstallman.dvback.global.oauth.handler.OAuth2SuccessHandler;
import org.richardstallman.dvback.global.oauth.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  // private final CorsConfigurationSource corsConfigurationSource;
  private final JwtAuthorizationFilter jwtAuthorizationFilter;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final OAuth2FailureHandler oAuth2FailureHandler;
  private final CustomOAuth2UserService customOAuth2UserService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(oauth2 -> oauth2
            .successHandler(oAuth2SuccessHandler)
            .failureHandler(oAuth2FailureHandler)
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))
        .build();

    // custom 필요
    //http.cors(cors -> cors.configurationSource(corsConfigurationSource));
    //http.formLogin(AbstractHttpConfigurer::disable);
    //http.httpBasic(AbstractHttpConfigurer::disable);
    //http.logout();
  }
}
