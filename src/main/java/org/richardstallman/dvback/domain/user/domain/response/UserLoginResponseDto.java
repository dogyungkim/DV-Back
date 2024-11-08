package org.richardstallman.dvback.domain.user.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import org.richardstallman.dvback.common.constant.CommonConstants;

public record UserLoginResponseDto(
    @NotNull String type,
    Long userId,
    String socialId,
    String email,
    String name,
    String nickname,
    String s3ProfileImageUrl,
    Boolean leave,
    CommonConstants.Gender gender,
    Date birthdate) {}
