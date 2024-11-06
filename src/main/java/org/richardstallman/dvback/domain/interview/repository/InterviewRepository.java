package org.richardstallman.dvback.domain.interview.repository;

import java.util.List;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;

public interface InterviewRepository {

  InterviewDomain save(InterviewDomain interviewDomain);

  InterviewDomain findById(Long interviewId);

  List<InterviewDomain> findInterviewsByUserId(Long userId);
}
