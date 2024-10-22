package org.richardstallman.dvback.domain.job.service;

import java.util.Optional;
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
  public JobDomain findJobById(Long jobId) {
    Optional<JobDomain> optionalJobDomain = jobRepository.findById(jobId);
    return optionalJobDomain.orElseThrow(
        () -> new IllegalArgumentException("job id에 해당하는 직무가 없습니다."));
  }
}
