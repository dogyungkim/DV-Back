package org.richardstallman.dvback.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;

public interface UserService {

  UserResponseDto getUserInfo(Long userId);

  void logout(HttpServletResponse response, String RefreshToken);

  boolean existsByUsername(String username);

  UserResponseDto updateUserInfo(Long userId, UserRequestDto userRequestDto);
}
