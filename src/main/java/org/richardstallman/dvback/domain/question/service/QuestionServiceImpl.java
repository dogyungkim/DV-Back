package org.richardstallman.dvback.domain.question.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalDomain;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.response.QuestionExternalResponseDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionInitialResponseDto;
import org.richardstallman.dvback.domain.question.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

  private final JobService jobService;
  private final InterviewConverter interviewConverter;
  private final QuestionConverter questionConverter;
  private final QuestionRepository questionRepository;

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${python.server.url}")
  private String pythonServerUrl;

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionInitialResponseDto getInitialQuestion(
      QuestionInitialRequestDto questionInitialRequestDto) {

    JobDomain jobDomain = jobService.findJobById(questionInitialRequestDto.jobId());

    if (questionInitialRequestDto.interviewStatus() != InterviewStatus.FILE_UPLOADED) {
      throw new IllegalArgumentException("Invalid interview status");
    }

    QuestionExternalRequestDto questionExternalRequestDto =
        questionConverter.reactRequestToPythonRequest(
            questionInitialRequestDto, jobDomain.getJobName());

    QuestionExternalResponseDto questionResponse =
        requestInitialQuestionFromPythonServer(questionExternalRequestDto);
    if (questionResponse == null || questionResponse.getQuestions().isEmpty()) {
      throw new IllegalStateException(
          "Python server returned an empty question list for job: " + jobDomain.getJobName());
    }

    List<QuestionExternalDomain> createdQuestions = questionResponse.getQuestions();
    boolean hasNext = createdQuestions.size() > 1;

    QuestionDomain firstQuestion =
        getCreatedQuestionDomain(questionInitialRequestDto, createdQuestions.get(0), jobDomain);
    QuestionDomain nextQuestion =
        hasNext
            ? getCreatedQuestionDomain(
                questionInitialRequestDto, createdQuestions.get(1), jobDomain)
            : null;

    for (int i = 2; i < createdQuestions.size(); i++) {
      getCreatedQuestionDomain(questionInitialRequestDto, createdQuestions.get(i), jobDomain);
    }

    InterviewCreateResponseDto interviewCreateResponseDto =
        interviewConverter.fromDomainToDto(
            interviewConverter.fromInterviewInitialQuestionRequestDtoToDomain(
                questionInitialRequestDto, jobDomain));

    return questionConverter.fromQuestionExternalDomainToQuestionInitialResponseDto(
        firstQuestion, interviewCreateResponseDto, nextQuestion, hasNext);
  }

  private QuestionDomain getCreatedQuestionDomain(
      QuestionInitialRequestDto questionInitialRequestDto,
      QuestionExternalDomain questionExternalDomain,
      JobDomain jobDomain) {
    return questionRepository.save(
        questionConverter.fromQuestionExternalDomainToDomain(
            questionExternalDomain,
            interviewConverter.fromInterviewInitialQuestionRequestDtoToDomain(
                questionInitialRequestDto, jobDomain)));
  }

  public QuestionExternalResponseDto requestInitialQuestionFromPythonServer(
      QuestionExternalRequestDto questionExternalRequestDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<QuestionExternalRequestDto> requestEntity =
        new HttpEntity<>(questionExternalRequestDto, headers);

    ResponseEntity<QuestionExternalResponseDto> response =
        restTemplate.exchange(
            pythonServerUrl + "/interview/questions",
            HttpMethod.POST,
            requestEntity,
            QuestionExternalResponseDto.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      throw new IllegalStateException("Failed to fetch questions from Python server");
    }
  }

  private Optional<QuestionDomain> getNextQuestion(Long interviewId, Long currentQuestionId) {
    List<QuestionDomain> questions = questionRepository.findQuestionsByInterviewId(interviewId);

    questions.sort(Comparator.comparingLong(QuestionDomain::getQuestionId));

    int currentIndex =
        IntStream.range(0, questions.size())
            .filter(i -> questions.get(i).getQuestionId().equals(currentQuestionId))
            .findFirst()
            .orElse(-1);

    if (currentIndex >= 0 && currentIndex < questions.size() - 1) {
      return Optional.of(questions.get(currentIndex + 1));
    } else {
      return Optional.empty();
    }
  }
}
