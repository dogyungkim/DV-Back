package org.richardstallman.dvback.domain.evaluation.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AnswerEvaluationExternalDomain {

  @NotNull @JsonProperty("feedback_text")
  String feedbackText;

  @NotNull @JsonProperty("question_id")
  long questionId;

  @NotNull @JsonProperty("score")
  int score;
}
