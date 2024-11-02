package org.richardstallman.dvback.domain.evaluation.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;

public record EvaluationExternalRequestDto(
    @JsonProperty("cover_letter") String coverLetterS3Url,
    @JsonProperty("questions") @NotNull List<String> questions,
    @JsonProperty("answers") @NotNull List<String> answers,
    @JsonProperty("interview_mode") @NotNull String interviewMode,
    @JsonProperty("interview_type") @NotNull String interviewType,
    @JsonProperty("interview_method") @NotNull String interviewMethod,
    @JsonProperty("job_role") @NotNull String jobName) {

  public EvaluationExternalRequestDto(
      String coverLetterS3Url,
      List<String> questions,
      List<String> answers,
      InterviewMode interviewMode,
      InterviewType interviewType,
      InterviewMethod interviewMethod,
      String jobName) {
    this(
        coverLetterS3Url,
        questions,
        answers,
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
