package org.richardstallman.dvback.domain.interview.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewConverter {

  public InterviewEntity toEntity(InterviewDomain interviewDomain) {
    return InterviewEntity.builder()
        .interviewId(interviewDomain.getInterviewId())
        .interviewStatus(interviewDomain.getInterviewStatus())
        .interviewType(interviewDomain.getInterviewType())
        .interviewMethod(interviewDomain.getInterviewMethod())
        .interviewMode(interviewDomain.getInterviewMode())
        .build();
  }

  public InterviewDomain toDomain(InterviewEntity interviewEntity) {
    return InterviewDomain.builder()
        .interviewId(interviewEntity.getInterviewId())
        .interviewStatus(interviewEntity.getInterviewStatus())
        .interviewType(interviewEntity.getInterviewType())
        .interviewMethod(interviewEntity.getInterviewMethod())
        .interviewMode(interviewEntity.getInterviewMode())
        .build();
  }
}
