package org.richardstallman.dvback.client.python;

import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.response.EvaluationExternalResponseDto;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalSttRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.response.QuestionExternalResponseDto;

public interface PythonService {

  QuestionExternalResponseDto getInterviewQuestions(
      QuestionExternalRequestDto requestDto, Long interviewId);

  EvaluationExternalResponseDto getOverallEvaluations(EvaluationExternalRequestDto requestDto);

  void sendAnswer(
      QuestionExternalSttRequestDto questionExternalSttRequestDto, Long interviewId, Long answerId);
}
