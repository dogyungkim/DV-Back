package org.richardstallman.dvback.common.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommonConstants {

  public enum InterviewStatus {
    INITIAL, // 최초 정보 입력 상태
    FILE_UPLOADED, // 자소서 및 파일 업로드 완료 상태
    WAITING_FOR_QUESTION, // 질문 생성 요청 후 대기 상태
    READY, // 면접 시작 가능 상태
    IN_PROGRESS, // 면접 진행 중 상태
    WAITING_FOR_FEEDBACK, // 피드백 생성 요청 후 대기 상태
    COMPLETED // 피드백 완료 상태
  }

  @Getter
  @AllArgsConstructor
  public enum InterviewType {
    TECHNICAL("technical"), // 기술 면접
    PERSONAL("personal"); // 인성 면접

    private final String pythonFormat;
  }

  @Getter
  @AllArgsConstructor
  public enum InterviewMode {
    REAL("real"), // 실전 면접 모드
    GENERAL("general"); // 일반/모의 면접 모드

    private final String pythonFormat;
  }

  @Getter
  @AllArgsConstructor
  public enum InterviewMethod {
    CHAT("chat"), // 채팅 면접
    VOICE("voice"), // 음성 면접
    VIDEO("video"); // 영상 면접

    private final String pythonFormat;
  }

  @AllArgsConstructor
  @Getter
  public enum ResponseCode {
    SUCCESS(200, "SUCCESS"),
    FAIL(400, "FAILED"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    private final int code;
    private final String message;
  }

  @Getter
  @AllArgsConstructor
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  public enum EvaluationCriteria {
    ANSWER("answer"),
    JOB_FIT("jobFit"),
    GROWTH_POTENTIAL("growth_potential"),
    TECHNICAL_DEPTH("technical_depth"),
    WORK_ATTITUDE("work_attitude");

    private final String pythonFormat;
  }

  public enum AnswerEvaluationScore {
    APPROPRIATE_RESPONSE,
    LOGICAL_FLOW,
    KEY_TERMS,
    CONSISTENCY,
    GRAMMATICAL_ERRORS
  }

  public enum PointTransactionType {
    DEPOSIT, // 적립
    WITHDRAWAL // 사용
  }

  @Getter
  @AllArgsConstructor
  public enum Gender {
    WOMAN, // 여성
    MAN // 남성
  }

  @Getter
  @AllArgsConstructor
  public enum FileType {
    COVER_LETTER("cover-letter"),
    RESUME("resume"),
    PORTFOLIO("portfolio"),
    AUDIO_ANSWER("audio-answer"),
    AUDIO_QUESTION("audio-question"),
    VIDEO_ANSWER("video-answer"),
    VIDEO_QUESTION("video-question");

    private final String folderName;
  }
}
