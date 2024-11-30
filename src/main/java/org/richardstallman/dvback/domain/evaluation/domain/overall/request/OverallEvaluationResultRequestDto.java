package org.richardstallman.dvback.domain.evaluation.domain.overall.request;

public record OverallEvaluationResultRequestDto(
    Long userId, Long interviewId, OverallEvaluationResultDto overallEvaluation) {}
