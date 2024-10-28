package org.richardstallman.dvback.domain.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.mock.interview.FakeInterviewRepository;

@Slf4j
public class InterviewServiceTest {

  @Mock private JobService jobService;

  @InjectMocks private InterviewServiceImpl interviewServiceImpl;
  @InjectMocks private InterviewConverter interviewConverter;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    FakeInterviewRepository fakeInterviewRepository = new FakeInterviewRepository();
    this.interviewServiceImpl =
        new InterviewServiceImpl(fakeInterviewRepository, jobService, interviewConverter);

    InterviewType interviewType = InterviewType.PERSONAL;
    InterviewMethod interviewMethod = InterviewMethod.CHAT;
    InterviewMode interviewMode = InterviewMode.GENERAL;
    JobDomain jobDomain =
        JobDomain.builder().jobId(1L).jobName("BACK_END").jobDescription("백엔드 직무입니다.").build();

    when(jobService.findJobById(1L)).thenReturn(jobDomain);

    fakeInterviewRepository.save(
        InterviewDomain.builder()
            .interviewType(interviewType)
            .interviewMethod(interviewMethod)
            .interviewMode(interviewMode)
            .job(jobDomain)
            .build());
  }

  @Test
  @DisplayName("면접 정보 입력 - 면접 저장 테스트")
  void create_interview_by_save_interview_info() {
    // given
    InterviewType interviewType = InterviewType.TECHNICAL;
    InterviewMethod interviewMethod = InterviewMethod.VIDEO;
    InterviewMode interviewMode = InterviewMode.REAL;

    InterviewCreateRequestDto interviewCreateRequestDto =
        new InterviewCreateRequestDto(interviewType, interviewMethod, interviewMode, 2L);

    // when
    InterviewCreateResponseDto interviewCreateResponseDto =
        interviewServiceImpl.createInterview(interviewCreateRequestDto);

    // then
    assertThat(interviewCreateResponseDto.interviewId()).isEqualTo(2);
    assertThat(interviewCreateResponseDto.interviewStatus()).isEqualTo(InterviewStatus.INITIAL);
    assertThat(interviewCreateResponseDto.interviewType()).isEqualTo(interviewType);
    assertThat(interviewCreateResponseDto.interviewMethod()).isEqualTo(interviewMethod);
    assertThat(interviewCreateResponseDto.interviewMode()).isEqualTo(interviewMode);
  }
}
