package org.richardstallman.dvback.client.python;

import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.response.QuestionExternalResponseDto;

public interface PythonService {

  QuestionExternalResponseDto getInterviewQuestions(QuestionExternalRequestDto requestDto);
}
