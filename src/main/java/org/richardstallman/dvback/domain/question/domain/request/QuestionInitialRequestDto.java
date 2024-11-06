package org.richardstallman.dvback.domain.question.domain.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;

public record QuestionInitialRequestDto(
    @NotNull Long interviewId,
    @NotNull String interviewTitle,
    @NotNull InterviewStatus interviewStatus,
    @NotNull InterviewType interviewType,
    @NotNull InterviewMethod interviewMethod,
    @NotNull InterviewMode interviewMode,
    List<FileRequestDto> files,
    @NotNull Long jobId) {}
