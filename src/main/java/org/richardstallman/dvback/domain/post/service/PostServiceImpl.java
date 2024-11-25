package org.richardstallman.dvback.domain.post.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.service.EvaluationService;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.domain.post.converter.PostConverter;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.post.domain.request.PostCreateRequestDto;
import org.richardstallman.dvback.domain.post.domain.response.PostCreateResponseDto;
import org.richardstallman.dvback.domain.post.repository.PostRepository;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.richardstallman.dvback.global.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final PostConverter postConverter;
  private final UserRepository userRepository;
  private final JobService jobService;
  private final InterviewService interviewService;
  private final EvaluationService evaluationService;

  @Override
  public PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId) {
    UserDomain userDomain = getUser(userId);
    JobDomain jobDomain = jobService.findJobByKoreanName(postCreateRequestDto.jobKoreanName());
    InterviewDomain interviewDomain =
        postCreateRequestDto.interviewId() == null
            ? null
            : interviewService.getInterviewById(postCreateRequestDto.interviewId());
    OverallEvaluationDomain overallEvaluationDomain =
        postCreateRequestDto.overallEvaluationId() == null
            ? null
            : evaluationService.getOverallEvaluationDomainById(
                postCreateRequestDto.overallEvaluationId());
    LocalDateTime generatedAt = TimeUtil.getCurrentDateTime();
    PostDomain postDomain =
        postRepository.save(
            postConverter.fromCreateRequestDtoToDomain(
                postCreateRequestDto,
                userDomain,
                jobDomain,
                interviewDomain,
                overallEvaluationDomain,
                generatedAt));
    InterviewResponseDto interviewResponseDto =
        interviewDomain == null
            ? null
            : interviewService.getInterviewResponseDtoByDomain(interviewDomain);
    OverallEvaluationResponseDto overallEvaluationResponseDto =
        overallEvaluationDomain == null
            ? null
            : evaluationService.getOverallEvaluation(
                new OverallEvaluationRequestDto(
                    overallEvaluationDomain.getInterviewDomain().getInterviewId()));

    return postConverter.fromDomainToCreateResponseDto(
        postDomain, interviewResponseDto, overallEvaluationResponseDto);
  }

  private UserDomain getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.NOT_FOUND, String.format("(%d): User Not Found", userId)));
  }
}
