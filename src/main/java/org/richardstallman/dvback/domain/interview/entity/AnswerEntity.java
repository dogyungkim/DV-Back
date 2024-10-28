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
import org.richardstallman.dvback.domain.question.entity.QuestionEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@Table(name = "answers")
public class AnswerEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long answerId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "question_id", nullable = false)
  private QuestionEntity question;

  @NotNull(message = "Answer Text is required") @Column(columnDefinition = "TEXT")
  private String answerText;

  private String s3AudioUrl;
  private String s3VideoUrl;
  private String answerType;
}
