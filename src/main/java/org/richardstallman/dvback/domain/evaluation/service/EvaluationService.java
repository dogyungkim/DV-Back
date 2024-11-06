package org.richardstallman.dvback.domain.evaluation.service;

import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationUserInfoListResponseDto;

public interface EvaluationService {

  OverallEvaluationResponseDto getOverallEvaluation(
      OverallEvaluationRequestDto overallEvaluationRequestDto);

  OverallEvaluationUserInfoListResponseDto getOverallEvaluationListByUserId(Long userId);

  OverallEvaluationResponseDto getOverallEvaluationByInterviewId(Long interviewId);
}
