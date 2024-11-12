package org.richardstallman.dvback.mock.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.repository.InterviewRepository;

@Slf4j
public class FakeInterviewRepository implements InterviewRepository {

  private final AtomicLong autoGeneratedId = new AtomicLong(0);
  private final List<InterviewDomain> data = new ArrayList<>();

  @Override
  public InterviewDomain save(InterviewDomain interviewDomain) {
    if (interviewDomain.getInterviewId() == null || interviewDomain.getInterviewId() == 0) {
      InterviewDomain savedData =
          InterviewDomain.builder()
              .interviewId(autoGeneratedId.incrementAndGet())
              .interviewType(interviewDomain.getInterviewType())
              .interviewStatus(interviewDomain.getInterviewStatus())
              .interviewMethod(interviewDomain.getInterviewMethod())
              .interviewMode(interviewDomain.getInterviewMode())
              .job(interviewDomain.getJob())
              .build();

      data.add(savedData);
      return savedData;
    }
    data.removeIf(item -> Objects.equals(item.getInterviewId(), interviewDomain.getInterviewId()));
    data.add(interviewDomain);
    return interviewDomain;
  }

  @Override
  public InterviewDomain findById(Long interviewId) {
    return data.stream()
        .filter(item -> Objects.equals(item.getInterviewId(), interviewId))
        .findAny()
        .orElse(null);
  }

  @Override
  public List<InterviewDomain> findInterviewsByUserId(Long userId) {
    return data.stream()
        .filter(item -> Objects.equals(item.getUserDomain().getId(), userId))
        .toList();
  }

  @Override
  public List<InterviewDomain> findInterviewsByUserIdWithEvaluation(Long userId) {
    return data.stream()
        .filter(
            item ->
                Objects.nonNull(item.getUserDomain().getId())
                    && item.getUserDomain().getId().equals(userId))
        .toList();
  }
}
