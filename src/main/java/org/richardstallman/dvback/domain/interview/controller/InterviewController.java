package org.richardstallman.dvback.domain.interview.controller;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/interview")
public class InterviewController {

  private final InterviewService interviewService;

  @PostMapping
  public ResponseEntity<InterviewCreateResponseDto> createInterview(
      @Validated @RequestBody final InterviewCreateRequestDto interviewCreateRequestDto) {
    final InterviewCreateResponseDto interviewCreateResponseDto =
        interviewService.createInterview(interviewCreateRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(interviewCreateResponseDto);
  }
}
