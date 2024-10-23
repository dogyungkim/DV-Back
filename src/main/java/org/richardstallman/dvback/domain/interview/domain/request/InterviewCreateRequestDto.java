package org.richardstallman.dvback.domain.interview.domain.request;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;

public record InterviewCreateRequestDto(
    @NotNull InterviewType interviewType,
    @NotNull InterviewMethod interviewMethod,
    @NotNull InterviewMode interviewMode,
    @NotNull Long jobId) {}
