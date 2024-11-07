package org.richardstallman.dvback.domain.evaluation.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record EvaluationExternalAnswerRequestDto(
    @JsonProperty("question_id") @NotNull Long questionId,
    @JsonProperty("answer_text") @NotNull String answerText) {}
