package org.richardstallman.dvback.domain.evaluation.repository.answer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnswerEvaluationRepositoryImpl implements AnswerEvaluationRepository {

  private final AnswerEvaluationConverter answerEvaluationConverter;
  private final AnswerEvaluationJpaRepository answerEvaluationJpaRepository;

  @Override
  public AnswerEvaluationDomain save(AnswerEvaluationDomain answerEvaluationDomain) {
    return answerEvaluationConverter.fromEntityToDomain(
        answerEvaluationJpaRepository.save(
            answerEvaluationConverter.fromDomainToEntity(answerEvaluationDomain)));
  }

  @Override
  public List<AnswerEvaluationDomain> saveAll(
      List<AnswerEvaluationDomain> answerEvaluationDomains) {
    return answerEvaluationJpaRepository
        .saveAll(
            answerEvaluationDomains.stream()
                .map(answerEvaluationConverter::fromDomainToEntity)
                .toList())
        .stream()
        .map(answerEvaluationConverter::fromEntityToDomain)
        .toList();
  }
}
