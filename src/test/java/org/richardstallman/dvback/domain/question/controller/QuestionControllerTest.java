package org.richardstallman.dvback.domain.question.controller;

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
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionInitialResponseDto;
import org.richardstallman.dvback.domain.question.service.QuestionService;
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
public class QuestionControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockBean private QuestionService questionService;

  @Test
  @DisplayName("질문 생성 - 최초 요청(모의 면접 성공) 테스트")
  void get_question_by_request_python_server() throws Exception {
    // given
    QuestionInitialRequestDto questionInitialRequestDto =
        new QuestionInitialRequestDto(
            1L,
            InterviewStatus.FILE_UPLOADED,
            InterviewType.TECHNICAL,
            InterviewMethod.VIDEO,
            InterviewMode.GENERAL,
            "",
            1L);
    String content = objectMapper.writeValueAsString(questionInitialRequestDto);

    when(questionService.getInitialQuestion(any()))
        .thenReturn(
            new QuestionInitialResponseDto(
                new InterviewCreateResponseDto(
                    1L,
                    InterviewStatus.FILE_UPLOADED,
                    InterviewType.TECHNICAL,
                    InterviewMethod.VIDEO,
                    InterviewMode.GENERAL,
                    JobDomain.builder()
                        .jobId(1L)
                        .jobName("BACK_END")
                        .jobDescription("백엔드 직무입니다.")
                        .build()),
                "스타크래프트를 처음으로 접한 경험을 통해 어떻게 최고를 목표로 삼고 성취했는지 이야기해보세요.",
                2L,
                true));
    ResultActions resultActions =
        mockMvc.perform(
            post("/question/initial-question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("interview.interviewId").value(1))
        .andExpect(jsonPath("interview.interviewStatus").value("FILE_UPLOADED"))
        .andExpect(jsonPath("interview.interviewType").value("TECHNICAL"))
        .andExpect(jsonPath("interview.interviewMethod").value("VIDEO"))
        .andExpect(jsonPath("interview.interviewMode").value("GENERAL"))
        .andExpect(jsonPath("interview.job.jobId").value(1L))
        .andExpect(jsonPath("interview.job.jobName").value("BACK_END"))
        .andExpect(jsonPath("interview.job.jobDescription").value("백엔드 직무입니다."))
        .andExpect(
            jsonPath("questionText").value("스타크래프트를 처음으로 접한 경험을 통해 어떻게 최고를 목표로 삼고 성취했는지 이야기해보세요."))
        .andExpect(jsonPath("nextQuestionId").value(2L))
        .andExpect(jsonPath("hasNext").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "질문 생성 - 최초 요청(모의 면접 성공)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("interviewId").type(JsonFieldType.NUMBER).description("면접 식별자"),
                fieldWithPath("interviewStatus").type(JsonFieldType.STRING).description("면접 상태"),
                fieldWithPath("interviewType").type(JsonFieldType.STRING).description("면접 유형"),
                fieldWithPath("interviewMethod").type(JsonFieldType.STRING).description("면접 방식"),
                fieldWithPath("interviewMode").type(JsonFieldType.STRING).description("면접 모드"),
                fieldWithPath("coverLetterS3Url")
                    .type(JsonFieldType.STRING)
                    .description("자소서 s3 url"),
                fieldWithPath("jobId").type(JsonFieldType.NUMBER).description("직무 식별자")),
            responseFields(
                fieldWithPath("interview.interviewId")
                    .type(JsonFieldType.NUMBER)
                    .description("면접 식별자"),
                fieldWithPath("interview.interviewStatus")
                    .type(JsonFieldType.STRING)
                    .description("면접 상태"),
                fieldWithPath("interview.interviewType")
                    .type(JsonFieldType.STRING)
                    .description("면접 유형"),
                fieldWithPath("interview.interviewMethod")
                    .type(JsonFieldType.STRING)
                    .description("면접 방식"),
                fieldWithPath("interview.interviewMode")
                    .type(JsonFieldType.STRING)
                    .description("면접 모드"),
                fieldWithPath("interview.job.jobId")
                    .type(JsonFieldType.NUMBER)
                    .description("직무 식별자"),
                fieldWithPath("interview.job.jobName")
                    .type(JsonFieldType.STRING)
                    .description("직무 이름"),
                fieldWithPath("interview.job.jobDescription")
                    .type(JsonFieldType.STRING)
                    .description("직무 설명"),
                fieldWithPath("questionText").type(JsonFieldType.STRING).description("질문 내용"),
                fieldWithPath("nextQuestionId").type(JsonFieldType.NUMBER).description("다음 질문 식별자"),
                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 질문 존재 여부"))));
  }
}
