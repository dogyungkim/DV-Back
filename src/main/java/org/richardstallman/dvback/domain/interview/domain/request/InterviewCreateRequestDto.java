package org.richardstallman.dvback.domain.interview.domain.request;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;

public record InterviewCreateRequestDto(
    @NotNull Long userId,
    @NotNull(message = "Interview Type is required") InterviewType interviewType,
    @NotNull(message = "Interview Method is required") InterviewMethod interviewMethod,
    @NotNull(message = "Interview Mode is required") InterviewMode interviewMode,
    @NotNull(message = "Job Id is required") Long jobId,
    String coverLetterUrl) {}
