package org.richardstallman.dvback.domain.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@Table(name = "questions")
public class QuestionEntity extends BaseEntity {

  @Id private Long questionId;

  @ManyToOne
  @JoinColumn(name = "interview_id")
  private InterviewEntity interview;

  @Column(columnDefinition = "TEXT")
  private String questionText;

  private String s3AudioUrl;
  private String s3VideoUrl;
  private String questionType;
}
