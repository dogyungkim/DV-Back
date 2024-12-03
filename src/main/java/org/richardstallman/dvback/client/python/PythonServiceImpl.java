package org.richardstallman.dvback.client.python;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.response.EvaluationExternalResponseDto;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalSttRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class PythonServiceImpl implements PythonService {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${python.server.url}/interview")
  private String pythonServerUrl;

  @Value("${python.server.question-path}")
  private String pythonServerQuestionPath;

  @Value("${python.server.evaluation-path}")
  private String pythonServerEvaluationPath;

  @Override
  public void requestQuestionList(QuestionExternalRequestDto requestDto, Long interviewId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<QuestionExternalRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

    URI uri =
        UriComponentsBuilder.fromUriString(pythonServerUrl)
            .path(String.format("/%d%s", interviewId, pythonServerQuestionPath))
            .build()
            .toUri();

    ResponseEntity<Object> response =
        restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Object.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException(
          "Failed to connect to Python server when Send Interview Info For Question List");
    }
  }

  @Override
  public void getOverallEvaluations(EvaluationExternalRequestDto requestDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<EvaluationExternalRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

    URI uri =
        UriComponentsBuilder.fromUriString(pythonServerUrl)
            .path(pythonServerEvaluationPath)
            .build()
            .toUri();

    ResponseEntity<EvaluationExternalResponseDto> response =
        restTemplate.exchange(
            uri, HttpMethod.POST, requestEntity, EvaluationExternalResponseDto.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      response.getBody();
    } else {
      throw new RuntimeException("Failed to connect to Python server");
    }
  }

  @Override
  public void sendAnswer(
      QuestionExternalSttRequestDto questionExternalSttRequestDto,
      Long interviewId,
      Long answerId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<QuestionExternalSttRequestDto> requestEntity =
        new HttpEntity<>(questionExternalSttRequestDto, headers);

    URI uri =
        UriComponentsBuilder.fromUriString(pythonServerUrl)
            .path(String.format("/%d/answer/%d", interviewId, answerId))
            .build()
            .toUri();

    ResponseEntity<Object> response =
        restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Object.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("Failed to connect to Python server when Send Answer For STT");
    }
  }
}
