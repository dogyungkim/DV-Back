package org.richardstallman.dvback.domain.answer.domain.request;

import jakarta.validation.constraints.NotNull;

public record AnswerPreviousRequestDto(
    @NotNull String answerText, String s3AudioUrl, String s3VideoUrl, String answerType) {}
