package org.richardstallman.dvback.client.python;

import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalSttRequestDto;

public interface PythonService {

  void requestQuestionList(QuestionExternalRequestDto requestDto, Long interviewId);

  void getOverallEvaluations(EvaluationExternalRequestDto requestDto);

  void sendAnswer(
      QuestionExternalSttRequestDto questionExternalSttRequestDto, Long interviewId, Long answerId);
}
