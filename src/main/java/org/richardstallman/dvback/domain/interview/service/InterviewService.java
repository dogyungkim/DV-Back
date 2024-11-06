package org.richardstallman.dvback.domain.interview.service;

import java.util.List;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewEvaluationResponseDto;

public interface InterviewService {

  InterviewCreateResponseDto createInterview(
      InterviewCreateRequestDto interviewCreateRequestDto, Long userId);

  List<InterviewEvaluationResponseDto> getInterviewsByUserId(Long userId);

  InterviewDomain getInterviewById(Long interviewId);
}
