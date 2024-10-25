package org.richardstallman.dvback.domain.job.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class JobRepositoryTest {

  @Autowired JobRepository jobRepository;

  @Test
  void save_job_entity_by_job_domain() {
    // given
    String jobName = "FRONT_END";
    String jobDescription = "프론트엔드 직무입니다.";

    JobDomain jobDomain =
        JobDomain.builder().jobName(jobName).jobDescription(jobDescription).build();

    // when
    JobDomain result = jobRepository.save(jobDomain);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getJobId()).isNotNull();
    assertThat(result.getJobName()).isNotNull();
    assertThat(result.getJobDescription()).isNotNull();

    Optional<JobDomain> retrievedEntityOptional = jobRepository.findById(result.getJobId());
    assertThat(retrievedEntityOptional).isPresent();

    JobDomain retrievedDomain = retrievedEntityOptional.get();
    assertThat(retrievedDomain.getJobName()).isEqualTo(jobName);
    assertThat(retrievedDomain.getJobDescription()).isEqualTo(jobDescription);
  }
}
