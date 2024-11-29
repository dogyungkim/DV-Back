package org.richardstallman.dvback.domain.evaluation.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationDto;

public record EvaluationExternalAnswerRequestDto(
    @JsonProperty("question_id") @NotNull Long questionId,
    @JsonProperty("answer") @NotNull AnswerEvaluationDto answer) {}
