package org.richardstallman.dvback.domain.question.service;

import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionInitialResponseDto;

public interface QuestionService {

  QuestionInitialResponseDto getInitialQuestion(
      QuestionInitialRequestDto questionInitialRequestDto);
}
