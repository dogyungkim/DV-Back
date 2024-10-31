package org.richardstallman.dvback.domain.evaluation.repository.answer;

import org.richardstallman.dvback.domain.evaluation.entity.answer.AnswerEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerEvaluationJpaRepository
    extends JpaRepository<AnswerEvaluationEntity, Long> {}
