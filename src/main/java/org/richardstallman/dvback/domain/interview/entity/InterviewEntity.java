package org.richardstallman.dvback.domain.interview.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.job.entity.JobEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "interviews")
public class InterviewEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long interviewId;

  private InterviewStatus interviewStatus;
  private InterviewType interviewType;
  private InterviewMethod interviewMethod;
  private InterviewMode interviewMode;

  @ManyToOne
  @JoinColumn(name = "job_id")
  private JobEntity job;
  //  private Long resumeId;
  //  private Long coverLetterId;
  //  private Long portfolioId;
  //  private Long userId;

}
