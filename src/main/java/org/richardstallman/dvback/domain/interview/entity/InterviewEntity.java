package org.richardstallman.dvback.domain.interview.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.job.entity.JobEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "interviews")
public class InterviewEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interview_seq")
  @SequenceGenerator(name = "interview_seq", sequenceName = "interview_id_seq", allocationSize = 1)
  private Long interviewId;

  @NotNull(message = "Interview Status is required") @Enumerated(EnumType.STRING)
  private InterviewStatus interviewStatus;

  @NotNull(message = "Interview Type is required") @Enumerated(EnumType.STRING)
  private InterviewType interviewType;

  @NotNull(message = "Interview Method is required") @Enumerated(EnumType.STRING)
  private InterviewMethod interviewMethod;

  @NotNull(message = "Interview Mode is required") @Enumerated(EnumType.STRING)
  private InterviewMode interviewMode;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "job_id", nullable = false)
  private JobEntity job;

  public InterviewEntity(
      InterviewStatus interviewStatus,
      InterviewType interviewType,
      InterviewMethod interviewMethod,
      InterviewMode interviewMode,
      JobEntity job) {
    super();
    this.interviewStatus = interviewStatus;
    this.interviewType = interviewType;
    this.interviewMethod = interviewMethod;
    this.interviewMode = interviewMode;
    this.job = job;
  }

  //  private Long resumeId;
  //  private Long coverLetterId;
  //  private Long portfolioId;
  //  private Long userId;
}
