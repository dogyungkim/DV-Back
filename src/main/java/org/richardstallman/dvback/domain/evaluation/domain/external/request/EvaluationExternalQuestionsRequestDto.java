package org.richardstallman.dvback.domain.evaluation.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record EvaluationExternalQuestionsRequestDto(
    @JsonProperty("questions") List<EvaluationExternalQuestionRequestDto> questions) {}
