package org.richardstallman.dvback.domain.question.controller;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionInitialResponseDto;
import org.richardstallman.dvback.domain.question.service.QuestionService;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<QuestionInitialResponseDto> getInitialQuestion(
      @Validated @RequestBody final QuestionInitialRequestDto questionInitialRequestDto) {
    final QuestionInitialResponseDto questionInitialResponseDto =
        questionService.getInitialQuestion(questionInitialRequestDto);
    return ResponseEntity.status(HttpStatus.OK).body(questionInitialResponseDto);
  }
}
