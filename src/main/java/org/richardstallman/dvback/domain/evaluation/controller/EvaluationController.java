package org.richardstallman.dvback.domain.evaluation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.service.EvaluationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/evaluation", produces = MediaType.APPLICATION_JSON_VALUE)
public class EvaluationController {

  private final EvaluationService evaluationService;

  @PostMapping
  public ResponseEntity<DvApiResponse<OverallEvaluationResponseDto>> getOverallEvaluationFromPython(
      @Valid @RequestBody final OverallEvaluationRequestDto overallEvaluationRequestDto) {
    final OverallEvaluationResponseDto overallEvaluationResponseDto =
        evaluationService.getOverallEvaluation(overallEvaluationRequestDto);
    return ResponseEntity.ok(DvApiResponse.of(overallEvaluationResponseDto));
  }

  @GetMapping("/{interviewId}")
  public ResponseEntity<DvApiResponse<OverallEvaluationResponseDto>>
      getOverallEvaluationByInterviewId(
          @AuthenticationPrincipal final Long userId, @PathVariable final Long interviewId) {
    final OverallEvaluationResponseDto overallEvaluationResponseDto =
        evaluationService.getOverallEvaluationByInterviewId(interviewId);
    return ResponseEntity.ok(DvApiResponse.of(overallEvaluationResponseDto));
  }
}
