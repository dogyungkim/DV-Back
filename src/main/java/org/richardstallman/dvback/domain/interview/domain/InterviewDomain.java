package org.richardstallman.dvback.domain.interview.domain;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.job.domain.JobDomain;

@Builder
@Getter
public class InterviewDomain {

  private final Long interviewId;
  private InterviewStatus interviewStatus;
  private InterviewType interviewType;
  private InterviewMethod interviewMethod;
  private InterviewMode interviewMode;
  private JobDomain job;

  //  private Long resumeId;
  //  private Long coverLetterId;
  //  private Long portfolioId;
  //  private Long userId;
}
