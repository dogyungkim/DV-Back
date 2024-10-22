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
@Table(name = "answers")
public class AnswerEntity extends BaseEntity {

  @Id private Long answerId;

  @ManyToOne
  @JoinColumn(name = "question_id")
  private QuestionEntity question;

  @Column(columnDefinition = "TEXT")
  private String answerText;

  private String s3AudioUrl;
  private String s3VideoUrl;
  private String answerType;
}
