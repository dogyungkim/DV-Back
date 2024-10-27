package org.richardstallman.dvback.domain.question.domain.external.response;

import java.util.List;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalDomain;

public record QuestionExternalResponseDto(List<QuestionExternalDomain> questions) {}
