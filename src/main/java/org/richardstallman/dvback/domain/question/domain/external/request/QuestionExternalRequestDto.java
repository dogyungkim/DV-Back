package org.richardstallman.dvback.domain.question.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;

public record QuestionExternalRequestDto(
    @JsonProperty("cover_letter_s3_url") String coverLetterS3Url,
    @JsonProperty("interview_mode") @NotNull String interviewMode,
    @JsonProperty("interview_type") @NotNull String interviewType,
    @JsonProperty("interview_method") @NotNull String interviewMethod,
    @JsonProperty("job_name") @NotNull String jobName) {

  public QuestionExternalRequestDto(
      String coverLetterS3Url,
      InterviewMode interviewMode,
      InterviewType interviewType,
      InterviewMethod interviewMethod,
      String jobName) {
    this(
        coverLetterS3Url,
        interviewMode.getPythonFormat(),
        interviewType.getPythonFormat(),
        interviewMethod.getPythonFormat(),
        convertJobNameToPythonFormat(jobName));
  }

  private static String convertJobNameToPythonFormat(String jobName) {
    return switch (jobName) {
      case "BACK_END" -> "backend";
      case "FRONTEND" -> "frontend";
      case "INFRA" -> "infra";
      case "AI" -> "ai";
      default -> jobName.toLowerCase();
    };
  }
}
