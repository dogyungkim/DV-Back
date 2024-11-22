package org.richardstallman.dvback.domain.user.service;

import java.io.IOException;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserPostService {

  UserResponseDto updateUserInfo(
      Long userId, UserRequestDto userRequestDto, MultipartFile profileImage) throws IOException;
}
