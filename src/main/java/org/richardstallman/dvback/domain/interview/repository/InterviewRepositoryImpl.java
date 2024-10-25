package org.richardstallman.dvback.domain.interview.repository;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InterviewRepositoryImpl implements InterviewRepository {

  private final InterviewJpaRepository interviewJpaRepository;
  private final InterviewConverter interviewConverter;

  @Override
  public InterviewDomain save(InterviewDomain interviewDomain) {
    return interviewConverter.fromEntityToDomain(
        interviewJpaRepository.save(
            interviewConverter.fromDomainToEntityWhenCreate(interviewDomain)));
  }
}
