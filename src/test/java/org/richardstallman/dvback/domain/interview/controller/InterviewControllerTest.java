package org.richardstallman.dvback.domain.interview.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.common.constant.CommonConstants.JobName;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class InterviewControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockBean private InterviewService interviewService;

  @Test
  @DisplayName("면접 정보 입력 - 면접 저장 테스트")
  void create_interview_by_save_interview_info() throws Exception {
    // given
    InterviewCreateRequestDto interviewCreateRequestDto =
        new InterviewCreateRequestDto(
            InterviewType.TECHNICAL, InterviewMethod.VIDEO, InterviewMode.REAL, 2L);
    String content = objectMapper.writeValueAsString(interviewCreateRequestDto);

    when(interviewService.createInterview(any()))
        .thenReturn(
            new InterviewCreateResponseDto(
                1L,
                InterviewStatus.INITIAL,
                InterviewType.TECHNICAL,
                InterviewMethod.VIDEO,
                InterviewMode.REAL,
                JobDomain.builder()
                    .jobId(2L)
                    .jobName(JobName.BACK_END)
                    .jobDescription("백엔드 입니다.")
                    .build()));
    ResultActions resultActions =
        mockMvc.perform(
            post("/interview").contentType(MediaType.APPLICATION_JSON).content(content));

    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("interviewId").value(1))
        .andExpect(jsonPath("interviewStatus").value("INITIAL"))
        .andExpect(jsonPath("interviewType").value("TECHNICAL"))
        .andExpect(jsonPath("interviewMethod").value("VIDEO"))
        .andExpect(jsonPath("interviewMode").value("REAL"))
        .andExpect(jsonPath("job.jobId").value(2L))
        .andExpect(jsonPath("job.jobName").value("BACK_END"))
        .andExpect(jsonPath("job.jobDescription").value("백엔드 입니다."));

    // restdocs
    resultActions.andDo(
        document(
            "면접 정보 입력 - 면접 저장",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("interviewType")
                    .type(JsonFieldType.STRING)
                    .description("면접 유형: TECHNICAL(기술 면접), PERSONAL(인성 면접)"),
                fieldWithPath("interviewMethod")
                    .type(JsonFieldType.STRING)
                    .description("면접 방식: CHAT(채팅 면접), VOICE(음성 면접), VIDEO(영상 면접)"),
                fieldWithPath("interviewMode")
                    .type(JsonFieldType.STRING)
                    .description("면접 모드: REAL(실전 면접 모드), PRACTICE(일반/모의 면접 모드)"),
                fieldWithPath("jobId").type(JsonFieldType.NUMBER).description("직무 식별자")),
            responseFields(
                fieldWithPath("interviewId").type(JsonFieldType.NUMBER).description("면접 식별자"),
                fieldWithPath("interviewStatus")
                    .type(JsonFieldType.STRING)
                    .description(
                        "면접 상태: INITIAL(최초 정보 입력 상태), FILE_UPLOADED(자소서 및 파일 업로드 완료 상태), WAITING_FOR_QUESTION(질문 생성 요청 후 대기 상태), (READY면접 시작 가능 상태), IN_PROGRESS(면접 진행 중 상태), WAITING_FOR_FEEDBACK(피드백 생성 요청 후 대기 상태), COMPLETED(피드백 완료 상태)"),
                fieldWithPath("interviewType")
                    .type(JsonFieldType.STRING)
                    .description("면접 유형: TECHNICAL(기술 면접), PERSONAL(인성 면접)"),
                fieldWithPath("interviewMethod")
                    .type(JsonFieldType.STRING)
                    .description("면접 방식: CHAT(채팅 면접), VOICE(음성 면접), VIDEO(영상 면접)"),
                fieldWithPath("interviewMode")
                    .type(JsonFieldType.STRING)
                    .description("면접 모드: REAL(실전 면접 모드), PRACTICE(일반/모의 면접 모드)"),
                fieldWithPath("job.jobId").type(JsonFieldType.NUMBER).description("직무 식별자"),
                fieldWithPath("job.jobName")
                    .type(JsonFieldType.STRING)
                    .description("직무 이름: BACK_END(백엔드), FRONT_END(프론트엔드), INFRA(인프라), AI(인공지능)"),
                fieldWithPath("job.jobDescription")
                    .type(JsonFieldType.STRING)
                    .description("직무 설명"))));
  }
}
