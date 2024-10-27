package org.richardstallman.dvback.mock.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.repository.JobRepository;

@Slf4j
public class FakeJobRepository implements JobRepository {

  private final AtomicLong autoGeneratedId = new AtomicLong(0);
  private final List<JobDomain> data = new ArrayList<>();

  public FakeJobRepository() {
    data.add(
        JobDomain.builder().jobId(1L).jobName("BACK_END").jobDescription("백엔드 직무입니다.").build());
    data.add(
        JobDomain.builder().jobId(2L).jobName("FRONT_END").jobDescription("프론트엔드 직무입니다.").build());
    data.add(JobDomain.builder().jobId(3L).jobName("INFRA").jobDescription("인프라 직무입니다.").build());
    data.add(JobDomain.builder().jobId(4L).jobName("AI").jobDescription("인공지능 직무입니다.").build());
  }

  @Override
  public JobDomain save(JobDomain jobDomain) {
    if (jobDomain.getJobId() == null || jobDomain.getJobId() == 0) {
      JobDomain savedData =
          JobDomain.builder()
              .jobId(autoGeneratedId.incrementAndGet())
              .jobName(jobDomain.getJobName())
              .jobDescription(jobDomain.getJobDescription())
              .build();
      data.add(savedData);
      return savedData;
    }
    data.removeIf(item -> Objects.equals(item.getJobId(), jobDomain.getJobId()));
    data.add(jobDomain);
    return jobDomain;
  }

  @Override
  public Optional<JobDomain> findById(Long jobId) {
    return data.stream().filter(item -> item.getJobId().equals(jobId)).findAny();
  }

  @Override
  public List<JobDomain> findAll() {
    return new ArrayList<>(data);
  }

  @Override
  public long count() {
    return data.size();
  }
}
