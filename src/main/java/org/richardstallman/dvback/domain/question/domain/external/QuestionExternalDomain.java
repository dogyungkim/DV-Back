package org.richardstallman.dvback.domain.question.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestionExternalDomain {

  @NotNull @JsonProperty("core_competency")
  List<String> coreCompetency;

  @NotNull @JsonProperty("model_answer")
  String modelAnswer;

  @NotNull @JsonProperty("question_id")
  int questionId;

  @NotNull @JsonProperty("question_intent")
  String questionIntent;

  @NotNull @JsonProperty("question_text")
  String questionText;
}
