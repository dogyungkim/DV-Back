package org.richardstallman.dvback.domain.evaluation.converter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.evaluation.entity.overall.OverallEvaluationEntity;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OverallEvaluationConverter {

  private final InterviewConverter interviewConverter;

  public OverallEvaluationEntity fromDomainToEntity(
      OverallEvaluationDomain overallEvaluationDomain) {
    return new OverallEvaluationEntity(
        overallEvaluationDomain.getOverallEvaluationId(),
        interviewConverter.fromDomainToEntity(overallEvaluationDomain.getInterviewDomain()));
  }

  public OverallEvaluationDomain fromEntityToDomain(
      OverallEvaluationEntity overallEvaluationEntity) {
    return OverallEvaluationDomain.builder()
        .overallEvaluationId(overallEvaluationEntity.getOverallEvaluationId())
        .interviewDomain(
            interviewConverter.fromEntityToDomain(overallEvaluationEntity.getInterview()))
        .build();
  }

  public OverallEvaluationResponseDto toResponseDto(
      InterviewDomain interviewDomain,
      List<EvaluationCriteriaResponseDto> evaluationCriteriaResponseDtos,
      List<AnswerEvaluationResponseDto> answerEvaluationResponseDtos) {
    return new OverallEvaluationResponseDto(
        interviewConverter.fromDomainToDto(interviewDomain),
        evaluationCriteriaResponseDtos,
        answerEvaluationResponseDtos);
  }
}
