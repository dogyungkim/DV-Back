package org.richardstallman.dvback.domain.question.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.python.PythonService;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.answer.converter.AnswerConverter;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewQuestionResponseDto;
import org.richardstallman.dvback.domain.interview.repository.InterviewRepository;
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
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
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
  private final InterviewRepository interviewRepository;
  private final QuestionConverter questionConverter;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final AnswerConverter answerConverter;
  private final UserRepository userRepository;

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionResponseDto getInitialQuestion(
      QuestionInitialRequestDto questionInitialRequestDto, Long userId) {

    JobDomain jobDomain = jobService.findJobById(questionInitialRequestDto.jobId());

    UserDomain userDomain =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, userId + " is not found"));
    QuestionExternalRequestDto questionExternalRequestDto =
        questionConverter.reactRequestToPythonRequest(
            userId, questionInitialRequestDto, jobDomain.getJobName());

    QuestionExternalResponseDto questionResponse =
        pythonService.getInterviewQuestions(
            questionExternalRequestDto, questionInitialRequestDto.interviewId());

    if (questionResponse == null || questionResponse.getQuestions().isEmpty()) {
      throw new IllegalStateException(
          "Python server returned an empty question list for job: " + jobDomain.getJobName());
    }

    CoverLetterDomain coverLetterDomain;
    if (questionInitialRequestDto.interviewMode() == InterviewMode.REAL) {
      coverLetterDomain =
          interviewRepository.findById(questionInitialRequestDto.interviewId()).getCoverLetter();
    } else {
      coverLetterDomain = null;
    }

    List<QuestionExternalDomain> createdQuestions = questionResponse.getQuestions();
    boolean hasNext = createdQuestions.size() > 1;

    QuestionDomain firstQuestion =
        getCreatedQuestionDomain(
            questionInitialRequestDto,
            createdQuestions.get(0),
            jobDomain,
            coverLetterDomain,
            userDomain);
    QuestionDomain nextQuestion =
        hasNext
            ? getCreatedQuestionDomain(
                questionInitialRequestDto,
                createdQuestions.get(1),
                jobDomain,
                coverLetterDomain,
                userDomain)
            : null;

    createdQuestions.stream()
        .skip(2)
        .forEach(
            q ->
                getCreatedQuestionDomain(
                    questionInitialRequestDto, q, jobDomain, coverLetterDomain, userDomain));

    InterviewDomain interviewDomain =
        interviewConverter.fromInterviewInitialQuestionRequestDtoToDomain(
            questionInitialRequestDto, jobDomain, coverLetterDomain, userDomain);

    InterviewQuestionResponseDto interviewQuestionResponseDto =
        interviewConverter.fromDomainToQuestionResponseDto(interviewDomain);

    return questionConverter.fromQuestionExternalDomainToQuestionResponseDto(
        firstQuestion, interviewQuestionResponseDto, nextQuestion, hasNext);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionResponseDto getNextQuestion(QuestionNextRequestDto questionNextRequestDto) {

    List<QuestionDomain> questions =
        questionRepository.findQuestionsByInterviewId(questionNextRequestDto.interviewId());

    QuestionDomain previousQuestion =
        findQuestionById(questions, questionNextRequestDto.answerQuestionId());
    QuestionDomain currentQuestion =
        questionNextRequestDto.nextQuestionId() == null
            ? null
            : findQuestionById(questions, questionNextRequestDto.nextQuestionId());
    QuestionDomain nextQuestion =
        questionNextRequestDto.nextQuestionId() == null
            ? null
            : findNextQuestion(questions, currentQuestion);
    boolean hasNext = nextQuestion != null;

    answerRepository.save(
        answerConverter.fromPreviousRequestDtoToDomain(
            questionNextRequestDto.answer(), previousQuestion));

    return questionConverter.fromQuestionExternalDomainToQuestionResponseDto(
        currentQuestion,
        interviewConverter.fromDomainToQuestionResponseDto(previousQuestion.getInterviewDomain()),
        nextQuestion,
        hasNext);
  }

  private QuestionDomain getCreatedQuestionDomain(
      QuestionInitialRequestDto questionInitialRequestDto,
      QuestionExternalDomain questionExternalDomain,
      JobDomain jobDomain,
      CoverLetterDomain coverLetterDomain,
      UserDomain userDomain) {
    return questionRepository.save(
        questionConverter.fromQuestionExternalDomainToDomain(
            questionExternalDomain,
            interviewConverter.fromInterviewInitialQuestionRequestDtoToDomain(
                questionInitialRequestDto, jobDomain, coverLetterDomain, userDomain)));
  }

  private QuestionDomain findQuestionById(List<QuestionDomain> questions, Long questionId) {
    return questions.stream()
        .filter(q -> q.getQuestionId().equals(questionId))
        .findFirst()
        .orElseThrow(
            () -> new ApiException(HttpStatus.BAD_REQUEST, questionId + " does not exist"));
  }

  private QuestionDomain findNextQuestionById(Long questionId) {
    return questionRepository.findById(questionId).orElse(null);
  }

  private QuestionDomain findNextQuestion(
      List<QuestionDomain> questions, QuestionDomain currentQuestion) {
    int currentIndex = questions.indexOf(currentQuestion);
    return (currentIndex < questions.size() - 1) ? questions.get(currentIndex + 1) : null;
  }
}
