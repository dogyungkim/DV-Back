package org.richardstallman.dvback.domain.job.service;

import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.repository.JobRepository;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;

  @Override
  public JobDomain createJob(JobDomain jobDomain) {
    return jobRepository.save(jobDomain);
  }

  @Override
  public JobDomain findJobById(Long jobId) {
    return jobRepository
        .findById(jobId)
        .orElseThrow(() -> new IllegalArgumentException("job id에 해당하는 직무가 없습니다."));
  }

  @Override
  public List<JobDomain> findAllJobs() {
    return jobRepository.findAll();
  }
}
