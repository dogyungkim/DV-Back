package org.richardstallman.dvback.domain.evaluation.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationFeedbackDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationScoreDto;

public record EvaluationExternalAnswerDto(
    @NotNull @JsonProperty("answer_text") String answerText,
    @JsonProperty("s3_audio_url") String s3AudioUrl,
    @JsonProperty("s3_video_url") String s3VideoUrl,
    @JsonProperty("scores") AnswerEvaluationScoreDto scores,
    @JsonProperty("feedback") AnswerEvaluationFeedbackDto feedback) {}
