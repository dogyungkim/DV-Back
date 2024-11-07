package org.richardstallman.dvback.domain.evaluation.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record EvaluationExternalAnswersRequestDto(
    @JsonProperty("answers") List<EvaluationExternalAnswerRequestDto> answers) {}
