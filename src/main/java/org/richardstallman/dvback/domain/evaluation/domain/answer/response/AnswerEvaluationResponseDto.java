package org.richardstallman.dvback.domain.evaluation.domain.answer.response;

import jakarta.validation.constraints.NotNull;

public record AnswerEvaluationResponseDto(
    @NotNull Long answerEvaluationId,
    @NotNull String questionText,
    @NotNull String answerText,
    @NotNull String answerFeedbackText,
    @NotNull int score) {}
