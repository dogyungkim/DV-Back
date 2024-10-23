package org.richardstallman.dvback.domain.interview.service;

import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;

public interface InterviewService {

  InterviewCreateResponseDto createInterview(InterviewCreateRequestDto interviewCreateRequestDto);
}
