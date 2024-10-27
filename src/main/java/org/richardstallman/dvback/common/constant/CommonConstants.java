package org.richardstallman.dvback.common.constant;

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

  public enum InterviewType {
    TECHNICAL, // 기술 면접
    PERSONAL // 인성 면접
  }

  public enum InterviewMode {
    REAL, // 실전 면접 모드
    GENERAL // 일반/모의 면접 모드
  }

  public enum InterviewMethod {
    CHAT, // 채팅 면접
    VOICE, // 음성 면접
    VIDEO // 영상 면접
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
}
