package org.richardstallman.dvback.domain.question.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;

public record QuestionExternalRequestDto(
    @JsonProperty("cover_letter_s3_url") String coverLetterS3Url,
    @JsonProperty("interview_mode") @NotNull InterviewMode interviewMode,
    @JsonProperty("interview_type") @NotNull InterviewType interviewType,
    @JsonProperty("interview_method") @NotNull InterviewMethod interviewMethod,
    @JsonProperty("job_name") @NotNull String jobName) {}
