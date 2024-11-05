package org.richardstallman.dvback.domain.question.domain.response;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewQuestionResponseDto;

public record QuestionResponseDto(
    @NotNull InterviewQuestionResponseDto interview,
    @NotNull String questionText,
    Long nextQuestionId,
    @NotNull Boolean hasNext) {}
