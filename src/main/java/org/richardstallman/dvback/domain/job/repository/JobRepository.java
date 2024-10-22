package org.richardstallman.dvback.domain.job.repository;

import java.util.Optional;
import org.richardstallman.dvback.domain.job.domain.JobDomain;

public interface JobRepository {

  JobDomain save(JobDomain jobDomain);

  Optional<JobDomain> findById(Long jobId);
}
