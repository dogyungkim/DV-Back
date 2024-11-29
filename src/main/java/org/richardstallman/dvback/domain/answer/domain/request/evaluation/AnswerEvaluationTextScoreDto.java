package org.richardstallman.dvback.domain.answer.domain.request.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore;

public record AnswerEvaluationTextScoreDto(
    @NotNull @JsonProperty("appropriate_response") AnswerEvaluationCriteriaDto appropriateResponse,
    @NotNull @JsonProperty("logical_flow") AnswerEvaluationCriteriaDto logicalFlow,
    @NotNull @JsonProperty("key_terms") AnswerEvaluationCriteriaDto keyTerms,
    @NotNull @JsonProperty("consistency") AnswerEvaluationCriteriaDto consistency,
    @NotNull @JsonProperty("grammatical_errors") AnswerEvaluationCriteriaDto grammaticalErrors) {

  public Map<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> toMap() {
    return Map.of(
        AnswerEvaluationScore.APPROPRIATE_RESPONSE, appropriateResponse,
        AnswerEvaluationScore.LOGICAL_FLOW, logicalFlow,
        AnswerEvaluationScore.KEY_TERMS, keyTerms,
        AnswerEvaluationScore.CONSISTENCY, consistency,
        AnswerEvaluationScore.GRAMMATICAL_ERRORS, grammaticalErrors);
  }
}
