package org.richardstallman.dvback.domain.interview.domain.response;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.job.domain.JobDomain;

@Getter
@Builder
public class InterviewCreateResponseDto {

  private final Long interviewId;
  private final InterviewStatus interviewStatus;
  private final InterviewType interviewType;
  private final InterviewMethod interviewMethod;
  private final InterviewMode interviewMode;
  private final JobDomain job;
}
