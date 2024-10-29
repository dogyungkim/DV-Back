package org.richardstallman.dvback.domain.question.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.client.python.PythonService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

  private final JobService jobService;
  private final PythonService pythonService;
  private final InterviewConverter interviewConverter;
  private final QuestionConverter questionConverter;
  private final QuestionRepository questionRepository;

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
        pythonService.getInterviewQuestions(questionExternalRequestDto);

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
