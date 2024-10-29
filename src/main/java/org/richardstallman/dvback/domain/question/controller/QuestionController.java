package org.richardstallman.dvback.domain.question.controller;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionNextRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionResponseDto;
import org.richardstallman.dvback.domain.question.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/question")
public class QuestionController {

  private final QuestionService questionService;

  @PostMapping("/initial-question")
  public ResponseEntity<DvApiResponse<QuestionResponseDto>> getInitialQuestion(
      @Validated @RequestBody final QuestionInitialRequestDto questionInitialRequestDto) {
    final QuestionResponseDto questionResponseDto =
        questionService.getInitialQuestion(questionInitialRequestDto);
    return ResponseEntity.ok(DvApiResponse.of(questionResponseDto));
  }

  @PostMapping("/next-question")
  public ResponseEntity<DvApiResponse<QuestionResponseDto>> getNextQuestion(
      @Validated @RequestBody final QuestionNextRequestDto questionNextRequestDto) {
    final QuestionResponseDto questionResponseDto =
        questionService.getNextQuestion(questionNextRequestDto);
    return ResponseEntity.ok(DvApiResponse.of(questionResponseDto));
  }
}
