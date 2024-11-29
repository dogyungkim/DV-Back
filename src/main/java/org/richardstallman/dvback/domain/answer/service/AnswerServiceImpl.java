package org.richardstallman.dvback.domain.answer.service;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationType;
import org.richardstallman.dvback.domain.answer.converter.AnswerConverter;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerEvaluationRequestDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationCriteriaDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationScoreDto;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationScoreDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.repository.answer.AnswerEvaluationRepository;
import org.richardstallman.dvback.domain.evaluation.repository.answer.score.AnswerEvaluationScoreRepository;
import org.richardstallman.dvback.domain.evaluation.repository.overall.OverallEvaluationRepository;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

  private final AnswerRepository answerRepository;
  private final OverallEvaluationRepository overallEvaluationRepository;
  private final AnswerEvaluationRepository answerEvaluationRepository;
  private final AnswerEvaluationScoreRepository answerEvaluationScoreRepository;
  private final AnswerConverter answerConverter;
  private final AnswerEvaluationConverter answerEvaluationConverter;

  @Override
  public void saveAnswerEvaluations(AnswerEvaluationRequestDto dto) {
    log.info("Start saving Answer and Evaluations for Question ID: {}", dto.questionId());

    AnswerDomain answerDomain = saveAnswer(dto);
    AnswerEvaluationDomain answerEvaluationDomain = saveAnswerEvaluation(dto, answerDomain);
    saveAnswerEvaluationScores(answerEvaluationDomain, dto.answer().scores());

    log.info("Successfully saved Answer and Evaluations for Question ID: {}", dto.questionId());
  }

  private AnswerDomain saveAnswer(AnswerEvaluationRequestDto dto) {
    AnswerDomain previousAnswer = getAnswerDomainFromQuestionId(dto.questionId());
    return answerRepository.save(
        answerConverter.fromSttEvaluationRequestDtoToDomain(dto, previousAnswer));
  }

  private AnswerEvaluationDomain saveAnswerEvaluation(
      AnswerEvaluationRequestDto dto, AnswerDomain answerDomain) {
    OverallEvaluationDomain overallEvaluationDomain =
        getOverallEvaluationDomainFromInterview(
            answerDomain.getQuestionDomain().getInterviewDomain());
    return answerEvaluationRepository.save(
        answerEvaluationConverter.sttEvaluationFeedbackDomainToDomain(
            dto.answer().feedback(), answerDomain, overallEvaluationDomain));
  }

  private void saveAnswerEvaluationScores(
      AnswerEvaluationDomain answerEvaluationDomain, @NotNull AnswerEvaluationScoreDto scores) {
    List<AnswerEvaluationScoreDomain> scoreDomains = new ArrayList<>();
    Map<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> textScores =
        scores.textScores().toMap();

    for (Map.Entry<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> entry :
        textScores.entrySet()) {
      scoreDomains.add(
          buildAnswerEvaluationScore(
              entry.getKey(), entry.getValue(), answerEvaluationDomain, AnswerEvaluationType.TEXT));
    }

    Map<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> voiceScores =
        scores.voiceScores().toMap();

    for (Map.Entry<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> entry :
        voiceScores.entrySet()) {
      scoreDomains.add(
          buildAnswerEvaluationScore(
              entry.getKey(),
              entry.getValue(),
              answerEvaluationDomain,
              AnswerEvaluationType.VOICE));
    }
    answerEvaluationScoreRepository.saveAll(scoreDomains);
  }

  private AnswerEvaluationScoreDomain buildAnswerEvaluationScore(
      AnswerEvaluationScore scoreName,
      AnswerEvaluationCriteriaDto scoreDetail,
      AnswerEvaluationDomain answerEvaluationDomain,
      AnswerEvaluationType type) {

    return AnswerEvaluationScoreDomain.builder()
        .answerEvaluationScoreName(scoreName)
        .score(scoreDetail.score())
        .rationale(scoreDetail.rationale())
        .answerEvaluationDomain(answerEvaluationDomain)
        .answerEvaluationType(type)
        .build();
  }

  private AnswerDomain getAnswerDomainFromQuestionId(Long questionId) {
    return answerRepository.findByQuestionId(questionId);
  }

  @Transactional
  protected OverallEvaluationDomain getOverallEvaluationDomainFromInterview(
      InterviewDomain interviewDomain) {
    OverallEvaluationDomain overallEvaluationDomain =
        overallEvaluationRepository.findByInterviewId(interviewDomain.getInterviewId());
    if (overallEvaluationDomain == null) {
      overallEvaluationDomain =
          overallEvaluationRepository.save(
              OverallEvaluationDomain.builder().interviewDomain(interviewDomain).build());
    }
    return overallEvaluationDomain;
  }
}
