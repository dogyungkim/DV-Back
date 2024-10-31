package org.richardstallman.dvback.domain.evaluation.repository.overall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.evaluation.converter.OverallEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.entity.overall.OverallEvaluationEntity;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OverallEvaluationRepositoryImpl implements OverallEvaluationRepository {

  private final OverallEvaluationConverter overallEvaluationConverter;
  private final OverallEvaluationJpaRepository overallEvaluationJpaRepository;

  @Override
  public OverallEvaluationDomain save(OverallEvaluationDomain overallEvaluationDomain) {
    OverallEvaluationEntity savedEntity =
        overallEvaluationJpaRepository.save(
            overallEvaluationConverter.fromDomainToEntity(overallEvaluationDomain));
    return overallEvaluationConverter.fromEntityToDomain(savedEntity);
  }
}
