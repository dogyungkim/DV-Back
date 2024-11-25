package org.richardstallman.dvback.domain.evaluation.service;

import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;

public interface EvaluationService {

  OverallEvaluationResponseDto getOverallEvaluation(
      OverallEvaluationRequestDto overallEvaluationRequestDto);

  OverallEvaluationResponseDto getOverallEvaluationByInterviewId(Long interviewId);

  OverallEvaluationDomain getOverallEvaluationDomainById(Long overallEvaluationId);
}
