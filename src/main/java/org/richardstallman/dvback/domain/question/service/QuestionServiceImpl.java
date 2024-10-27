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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

  private final JobService jobService;
  private final InterviewConverter interviewConverter;
  private final QuestionConverter questionConverter;
  private final QuestionRepository questionRepository;

  @Override
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
    if (questionResponse == null || questionResponse.questions().isEmpty()) {
      throw new IllegalStateException(
          "Python server returned an empty question list for job: " + jobDomain.getJobName());
    }

    List<QuestionExternalDomain> createdQuestions = questionResponse.questions();
    boolean hasNext = createdQuestions.size() > 1;

    getCreatedQuestionDomain(questionInitialRequestDto, createdQuestions.get(0), jobDomain);

    QuestionDomain nextQuestion =
        (createdQuestions.size() == 2)
            ? getCreatedQuestionDomain(
                questionInitialRequestDto, createdQuestions.get(1), jobDomain)
            : null;

    InterviewCreateResponseDto interviewCreateResponseDto =
        interviewConverter.fromDomainToDto(
            interviewConverter.fromInterviewInitialQuestionRequestDtoToDomain(
                questionInitialRequestDto, jobDomain));

    return questionConverter.fromInterviewQuestionDomainToInterviewInitialQuestionResponseDto(
        createdQuestions.get(0), interviewCreateResponseDto, nextQuestion, hasNext);
  }

  private QuestionDomain getCreatedQuestionDomain(
      QuestionInitialRequestDto questionInitialRequestDto,
      QuestionExternalDomain questionExternalDomain,
      JobDomain jobDomain) {
    return questionRepository.save(
        questionConverter.fromInterviewQuestionDomainToDomain(
            questionExternalDomain,
            interviewConverter.fromInterviewInitialQuestionRequestDtoToDomain(
                questionInitialRequestDto, jobDomain)));
  }

  private QuestionExternalResponseDto requestInitialQuestionFromPythonServer(
      QuestionExternalRequestDto questionExternalRequestDto) {
    // 파이썬 면접 질문 목록 요청 로직 - 질문 생성 task 시 작성 예정
    return null;
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
