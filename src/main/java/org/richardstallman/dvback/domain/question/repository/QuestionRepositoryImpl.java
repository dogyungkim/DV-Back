package org.richardstallman.dvback.domain.question.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

  private final QuestionJpaRepository questionJpaRepository;
  private final QuestionConverter questionConverter;

  @Override
  public QuestionDomain save(QuestionDomain questionDomain) {
    return questionConverter.fromEntityToDomain(
        questionJpaRepository.save(questionConverter.fromDomainToEntity(questionDomain)));
  }

  @Override
  public List<QuestionDomain> findQuestionsByInterviewId(Long interviewId) {
    return questionJpaRepository.findByInterviewInterviewId(interviewId).stream()
        .map(questionConverter::fromEntityToDomain)
        .toList();
  }
}
