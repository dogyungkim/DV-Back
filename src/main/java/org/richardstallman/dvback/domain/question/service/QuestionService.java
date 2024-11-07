package org.richardstallman.dvback.domain.question.service;

import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionNextRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionResponseDto;

public interface QuestionService {

  QuestionResponseDto getInitialQuestion(
      QuestionInitialRequestDto questionInitialRequestDto, Long userId);

  QuestionResponseDto getNextQuestion(QuestionNextRequestDto questionNextRequestDto);
}
