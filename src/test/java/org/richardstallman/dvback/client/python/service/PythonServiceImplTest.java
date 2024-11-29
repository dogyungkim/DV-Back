package org.richardstallman.dvback.client.python.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.richardstallman.dvback.client.python.PythonServiceImpl;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalContentDomain;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalDomain;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.response.QuestionExternalResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = PythonServiceImpl.class)
@ActiveProfiles("test")
public class PythonServiceImplTest {

  @InjectMocks private PythonServiceImpl pythonService;

  @Value("${python.server.url}/interview")
  private String pythonServerUrl;

  @Value("${python.server.question-path}")
  private String pythonServerQuestionPath;

  @Value("${python.server.evaluation-path}")
  private String pythonServerEvaluationPath;

  @Mock private RestTemplate restTemplate;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    this.pythonService =
        new PythonServiceImpl(
            pythonServerUrl, pythonServerQuestionPath, pythonServerEvaluationPath);
  }

  @Test
  void get_interview_questions_general_technical_chat_success() {
    // given
    Long userId = 1L;
    Long interviewId = 1L;
    InterviewMode interviewMode = InterviewMode.GENERAL;
    InterviewType interviewType = InterviewType.TECHNICAL;
    InterviewMethod interviewMethod = InterviewMethod.CHAT;
    int questionCount = 3;
    String jobName = "backend";
    String[] filePaths = new String[] {};
    QuestionExternalRequestDto requestDto =
        new QuestionExternalRequestDto(
            userId,
            interviewMode.getPythonFormat(),
            interviewType.getPythonFormat(),
            interviewMethod.getPythonFormat(),
            questionCount,
            jobName,
            filePaths);
    Long questionId1 = 1L;
    Long questionId2 = 2L;
    Long questionId3 = 3L;

    QuestionExternalContentDomain questionExternalContentDomain1 =
        QuestionExternalContentDomain.builder()
            .questionText("첫 번째 질문입니다.")
            .s3VideoUrl(null)
            .s3VideoUrl(null)
            .build();
    QuestionExternalContentDomain questionExternalContentDomain2 =
        QuestionExternalContentDomain.builder()
            .questionText("두 번째 질문입니다.")
            .s3VideoUrl(null)
            .s3VideoUrl(null)
            .build();
    QuestionExternalContentDomain questionExternalContentDomain3 =
        QuestionExternalContentDomain.builder()
            .questionText("세 번째 질문입니다.")
            .s3VideoUrl(null)
            .s3VideoUrl(null)
            .build();
    String questionExcerpt = null;
    String questionIntent = "질문 의도입니다.";
    List<String> keyTerms = new ArrayList<>();
    keyTerms.add("핵심 키워드1");
    keyTerms.add("핵심 키워드2");
    QuestionExternalDomain questionExternalDomain1 =
        QuestionExternalDomain.builder()
            .questionId(questionId1)
            .question(questionExternalContentDomain1)
            .questionExcerpt(questionExcerpt)
            .questionIntent(questionIntent)
            .keyTerms(keyTerms)
            .build();
    QuestionExternalDomain questionExternalDomain2 =
        QuestionExternalDomain.builder()
            .questionId(questionId2)
            .question(questionExternalContentDomain2)
            .questionExcerpt(questionExcerpt)
            .questionIntent(questionIntent)
            .keyTerms(keyTerms)
            .build();
    QuestionExternalDomain questionExternalDomain3 =
        QuestionExternalDomain.builder()
            .questionId(questionId3)
            .question(questionExternalContentDomain3)
            .questionExcerpt(questionExcerpt)
            .questionIntent(questionIntent)
            .keyTerms(keyTerms)
            .build();
    List<QuestionExternalDomain> questionExternalDomains = new ArrayList<>();
    questionExternalDomains.add(questionExternalDomain1);
    questionExternalDomains.add(questionExternalDomain2);
    questionExternalDomains.add(questionExternalDomain3);

    QuestionExternalResponseDto expectedResponse =
        new QuestionExternalResponseDto(questionExternalDomains);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<QuestionExternalRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

    URI uri =
        UriComponentsBuilder.fromUriString(pythonServerUrl)
            .path(String.format("/%d%s", interviewId, pythonServerQuestionPath))
            .build()
            .toUri();
    ResponseEntity<QuestionExternalResponseDto> mockResponse =
        new ResponseEntity<>(expectedResponse, HttpStatus.OK);

    when(restTemplate.exchange(
            uri, HttpMethod.POST, requestEntity, QuestionExternalResponseDto.class))
        .thenReturn(mockResponse);
    ResponseEntity<QuestionExternalResponseDto> response =
        restTemplate.exchange(
            uri, HttpMethod.POST, requestEntity, QuestionExternalResponseDto.class);
    // when
    QuestionExternalResponseDto actualResponse =
        pythonService.getInterviewQuestions(requestDto, interviewId);

    // then
    assertNotNull(actualResponse);
    assertEquals(expectedResponse.getQuestions().size(), actualResponse.getQuestions().size());
    verify(restTemplate, times(1))
        .exchange(uri, HttpMethod.POST, requestEntity, QuestionExternalResponseDto.class);
  }
}
