package org.richardstallman.dvback.mock.question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.repository.QuestionRepository;

@Slf4j
public class FakeQuestionRepository implements QuestionRepository {

  private final AtomicLong autoGeneratedId = new AtomicLong(0);
  private final List<QuestionDomain> data = new ArrayList<>();

  @Override
  public QuestionDomain save(QuestionDomain questionDomain) {
    if (questionDomain.getQuestionId() == null || questionDomain.getQuestionId() == 0) {
      QuestionDomain savedData =
          QuestionDomain.builder()
              .questionId(autoGeneratedId.incrementAndGet())
              .interviewDomain(questionDomain.getInterviewDomain())
              .questionText(questionDomain.getQuestionText())
              .s3AudioUrl(questionDomain.getS3AudioUrl())
              .s3VideoUrl(questionDomain.getS3VideoUrl())
              .questionType(questionDomain.getQuestionType())
              .build();
      data.add(savedData);
      return savedData;
    }
    data.removeIf(item -> Objects.equals(item.getQuestionId(), questionDomain.getQuestionId()));
    data.add(questionDomain);
    return questionDomain;
  }

  @Override
  public Optional<QuestionDomain> findById(Long questionId) {
    return data.stream().filter(item -> item.getQuestionId().equals(questionId)).findAny();
  }

  @Override
  public List<QuestionDomain> findQuestionsByInterviewId(Long interviewId) {
    return data.stream()
        .filter(item -> item.getInterviewDomain().getInterviewId().equals(interviewId))
        .toList();
  }
}
