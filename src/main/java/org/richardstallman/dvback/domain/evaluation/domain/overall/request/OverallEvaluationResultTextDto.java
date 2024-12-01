package org.richardstallman.dvback.domain.evaluation.domain.overall.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record OverallEvaluationResultTextDto(
    @NotNull @JsonProperty("job_fit") OverallEvaluationResultCriteriaDto jobFit,
    @NotNull @JsonProperty("growth_potential") OverallEvaluationResultCriteriaDto growthPotential,
    @NotNull @JsonProperty("work_attitude") OverallEvaluationResultCriteriaDto workAttitude,
    @NotNull @JsonProperty("technical_depth") OverallEvaluationResultCriteriaDto technicalDepth) {}
