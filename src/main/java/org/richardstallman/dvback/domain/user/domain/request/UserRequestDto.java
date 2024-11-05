package org.richardstallman.dvback.domain.user.domain.request;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants;

import java.util.Date;

public record UserRequestDto (
    @NotNull(message = "Nickname is required") String nickname,
    @NotNull(message = "Birthdate is required") Date birthdate,
    @NotNull(message = "Gender is required") CommonConstants.Gender gender) {}
