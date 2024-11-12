package org.richardstallman.dvback.domain.interview.repository;

import java.util.List;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewJpaRepository extends JpaRepository<InterviewEntity, Long> {

  List<InterviewEntity> findByUserIdOrderByInterviewIdDesc(Long userId);

  @EntityGraph(attributePaths = {"overallEvaluation"})
  @Query(
      "SELECT i FROM InterviewEntity i WHERE i.user.id = :userId AND i.overallEvaluation IS NOT NULL ORDER BY i.interviewId DESC")
  List<InterviewEntity> findByUserIdWithEvaluationsOrderByInterviewIdDesc(
      @Param("userId") Long userId);
}
