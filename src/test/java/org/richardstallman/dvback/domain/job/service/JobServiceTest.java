package org.richardstallman.dvback.domain.job.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.richardstallman.dvback.common.constant.CommonConstants.JobName;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.mock.job.FakeJobRepository;

@Slf4j
public class JobServiceTest {

  @InjectMocks private JobServiceImpl jobServiceImpl;
  private FakeJobRepository fakeJobRepository;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    this.fakeJobRepository = new FakeJobRepository();
    this.jobServiceImpl = new JobServiceImpl(fakeJobRepository);

    JobName jobName = JobName.BACK_END;
    String jobDescription = "백엔드 직무입니다.";

    fakeJobRepository.save(
        JobDomain.builder().jobName(jobName).jobDescription(jobDescription).build());
  }

  @Test
  void create_job_by_save_job_info() {
    // given
    JobName jobName = JobName.FRONT_END;
    String jobDescription = "프론트엔드 직무입니다.";

    JobDomain givenJobDomain =
        JobDomain.builder().jobName(jobName).jobDescription(jobDescription).build();

    // when
    JobDomain createdJobDomain = jobServiceImpl.createJob(givenJobDomain);

    // then
    assertThat(createdJobDomain.getJobId()).isEqualTo(2);
    assertThat(createdJobDomain.getJobName()).isEqualTo(jobName);
    assertThat(createdJobDomain.getJobDescription()).isEqualTo(jobDescription);
  }

  @Test
  void get_job_by_id() {
    // given

    // when
    JobDomain result = jobServiceImpl.findJobById(1L);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getJobId()).isEqualTo(1);
  }

  @Test
  void get_all_jobs() {
    // given
    JobDomain job1 =
        JobDomain.builder().jobName(JobName.BACK_END).jobDescription("백엔드 입니다.").build();
    JobDomain job2 =
        JobDomain.builder().jobName(JobName.FRONT_END).jobDescription("프론트엔드 입니다.").build();
    fakeJobRepository.save(job1);
    fakeJobRepository.save(job2);

    // when
    List<JobDomain> result = jobServiceImpl.findAllJobs();

    // then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
  }
}
