package org.richardstallman.dvback.domain.job.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.entity.JobEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobConverter {

  public JobEntity toEntity(JobDomain jobDomain) {
    return JobEntity.builder()
        .jobId(jobDomain.getJobId())
        .jobName(jobDomain.getJobName())
        .jobDescription(jobDomain.getJobDescription())
        .build();
  }

  public JobDomain toDomain(JobEntity jobEntity) {
    return JobDomain.builder()
        .jobId(jobEntity.getJobId())
        .jobName(jobEntity.getJobName())
        .jobDescription(jobEntity.getJobDescription())
        .build();
  }
}
