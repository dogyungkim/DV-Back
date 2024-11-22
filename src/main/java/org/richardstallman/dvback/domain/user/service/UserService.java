package org.richardstallman.dvback.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserResponseDto updateUserInfo(
      Long userId, UserRequestDto userRequestDto, MultipartFile profileImage) throws IOException;

  UserResponseDto getUserInfo(Long userId);

  void logout(HttpServletResponse response, String RefreshToken);

  boolean existsByUsername(String username);

  String getProfileImage(Long userId);
}
