package org.richardstallman.dvback.domain.interview.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/interview", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterviewController {

  private final InterviewService interviewService;

  @PostMapping
  public ResponseEntity<DvApiResponse<InterviewCreateResponseDto>> createInterview(
      @Valid @RequestBody final InterviewCreateRequestDto interviewCreateRequestDto) {
    final InterviewCreateResponseDto interviewCreateResponseDto =
        interviewService.createInterview(interviewCreateRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(DvApiResponse.of(interviewCreateResponseDto));
  }
}
