package org.richardstallman.dvback.domain.question.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalSttAnswerRequestDomain;

public record QuestionExternalSttRequestDto(
    @NotNull @JsonProperty("interview_method") String interviewMethod,
    @NotNull @JsonProperty("user_id") Long userId,
    @NotNull @JsonProperty("answer") QuestionExternalSttAnswerRequestDomain answer) {}
