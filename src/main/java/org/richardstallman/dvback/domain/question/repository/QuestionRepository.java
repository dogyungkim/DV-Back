package org.richardstallman.dvback.domain.question.repository;

import java.util.List;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;

public interface QuestionRepository {

  QuestionDomain save(QuestionDomain questionDomain);

  List<QuestionDomain> findQuestionsByInterviewId(Long interviewId);
}
