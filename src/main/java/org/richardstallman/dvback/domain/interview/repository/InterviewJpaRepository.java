package org.richardstallman.dvback.domain.interview.repository;

import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewJpaRepository extends JpaRepository<InterviewEntity, Long> {}
