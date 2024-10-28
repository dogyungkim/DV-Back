package org.richardstallman.dvback.domain.question.domain.response;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;

public record QuestionInitialResponseDto(
    @NotNull InterviewCreateResponseDto interview,
    @NotNull String questionText,
    Long nextQuestionId,
    @NotNull Boolean hasNext) {}
