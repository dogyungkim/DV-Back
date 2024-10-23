package org.richardstallman.dvback.domain.interview.repository;

import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;

public interface InterviewRepository {

  InterviewDomain save(InterviewDomain interviewDomain);
}
