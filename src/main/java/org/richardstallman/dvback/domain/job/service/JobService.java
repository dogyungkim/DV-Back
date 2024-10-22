package org.richardstallman.dvback.domain.job.service;

import org.richardstallman.dvback.domain.job.domain.JobDomain;

public interface JobService {

  JobDomain findJobById(Long jobId);
}
