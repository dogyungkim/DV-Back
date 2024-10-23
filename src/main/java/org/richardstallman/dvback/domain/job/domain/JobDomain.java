package org.richardstallman.dvback.domain.job.domain;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.JobName;

@Builder
@Getter
public class JobDomain {

  private Long jobId;
  private JobName jobName;
  private String jobDescription;
}
