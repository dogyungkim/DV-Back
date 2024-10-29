package org.richardstallman.dvback.domain.question.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.python.PythonService;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.domain.answer.converter.AnswerConverter;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
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
import org.richardstallman.dvback.domain.question.domain.request.QuestionNextRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionResponseDto;
import org.richardstallman.dvback.domain.question.repository.QuestionRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

  private final JobService jobService;
  private final PythonService pythonService;
  private final InterviewConverter interviewConverter;
  private final QuestionConverter questionConverter;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final AnswerConverter answerConverter;

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionResponseDto getInitialQuestion(
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

    createdQuestions.stream()
        .skip(2)
        .forEach(q -> getCreatedQuestionDomain(questionInitialRequestDto, q, jobDomain));

    InterviewCreateResponseDto interviewCreateResponseDto =
        interviewConverter.fromDomainToDto(
            interviewConverter.fromInterviewInitialQuestionRequestDtoToDomain(
                questionInitialRequestDto, jobDomain));

    return questionConverter.fromQuestionExternalDomainToQuestionResponseDto(
        firstQuestion, interviewCreateResponseDto, nextQuestion, hasNext);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionResponseDto getNextQuestion(QuestionNextRequestDto questionNextRequestDto) {

    List<QuestionDomain> questions =
        questionRepository.findQuestionsByInterviewId(questionNextRequestDto.interviewId());

    QuestionDomain previousQuestion =
        findQuestionById(questions, questionNextRequestDto.questionId());
    QuestionDomain nextQuestion = findNextQuestion(questions, previousQuestion);
    boolean hasNext = nextQuestion != null;

    answerRepository.save(
        answerConverter.fromPreviousRequestDtoToDomain(
            questionNextRequestDto.answer(), previousQuestion));

    return questionConverter.fromQuestionExternalDomainToQuestionResponseDto(
        previousQuestion,
        interviewConverter.fromDomainToDto(previousQuestion.getInterviewDomain()),
        nextQuestion,
        hasNext);
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

  private QuestionDomain findQuestionById(List<QuestionDomain> questions, Long questionId) {
    return questions.stream()
        .filter(q -> q.getQuestionId().equals(questionId))
        .findFirst()
        .orElseThrow(
            () -> new ApiException(HttpStatus.BAD_REQUEST, questionId + " does not exist"));
  }

  private QuestionDomain findNextQuestion(
      List<QuestionDomain> questions, QuestionDomain currentQuestion) {
    int currentIndex = questions.indexOf(currentQuestion);
    return (currentIndex < questions.size() - 1) ? questions.get(currentIndex + 1) : null;
  }
}
