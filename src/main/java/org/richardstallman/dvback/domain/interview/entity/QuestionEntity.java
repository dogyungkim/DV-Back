package org.richardstallman.dvback.domain.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@Table(name = "questions")
public class QuestionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long questionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interview_id", nullable = false)
  private InterviewEntity interview;

  @NotNull(message = "Question Text is required") @Column(columnDefinition = "TEXT")
  private String questionText;

  private String s3AudioUrl;
  private String s3VideoUrl;
  private String questionType;
}
