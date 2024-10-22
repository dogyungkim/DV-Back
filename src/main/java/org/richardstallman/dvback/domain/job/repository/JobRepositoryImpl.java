package org.richardstallman.dvback.domain.job.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.job.converter.JobConverter;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {

  private final JobJpaRepository jobJpaRepository;
  private final JobConverter jobConverter;

  @Override
  public JobDomain save(JobDomain jobDomain) {
    return jobConverter.toDomain(jobJpaRepository.save(jobConverter.toEntity(jobDomain)));
  }

  @Override
  public Optional<JobDomain> findById(Long jobId) {

    return jobJpaRepository.findById(jobId).map(jobConverter::toDomain);
  }
}
