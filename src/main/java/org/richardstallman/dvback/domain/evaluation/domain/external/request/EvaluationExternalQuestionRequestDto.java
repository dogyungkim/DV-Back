package org.richardstallman.dvback.domain.evaluation.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EvaluationExternalQuestionRequestDto(
    @JsonProperty("question_id") @NotNull Long questionId,
    @JsonProperty("question_excerpt") @NotNull String questionExcerpt,
    @JsonProperty("question_text") @NotNull String questionText,
    @JsonProperty("question_intent") @NotNull String questionIntent,
    @JsonProperty("key_terms") @NotNull List<String> keyTerms) {}
