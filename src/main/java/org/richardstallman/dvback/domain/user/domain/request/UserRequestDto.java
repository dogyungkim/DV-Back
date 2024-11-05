package org.richardstallman.dvback.domain.user.domain.request;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import org.richardstallman.dvback.common.constant.CommonConstants;

public record UserRequestDto(
    @NotNull(message = "Nickname is required") String nickname,
    @NotNull(message = "Birthdate is required") Date birthdate,
    @NotNull(message = "Gender is required") CommonConstants.Gender gender) {}
