package org.richardstallman.dvback.domain.interview.domain.request;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;

@Getter
@Builder
public class InterviewCreateRequestDto {

  private final InterviewType interviewType;
  private final InterviewMethod interviewMethod;
  private final InterviewMode interviewMode;
  private final Long jobId;
}
