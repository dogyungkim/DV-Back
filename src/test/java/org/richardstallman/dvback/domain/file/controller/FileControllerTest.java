package org.richardstallman.dvback.domain.file.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class FileControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtUtil jwtUtil;

  @MockBean private S3Service s3Service;

  @Test
  @DisplayName("면접 정보 입력 시 PreSigned URL 생성")
  @WithMockUser
  void testGetCoverLetterUploadUrlWhenInputInterviewInfo() throws Exception {
    // Given
    Long userId = 1L;
    Long interviewId = 2L;
    String fileName = "testFile.txt";
    String expectedUrl = "https://s3.aws.com/testFile.txt";

    String accessToken = jwtUtil.generateAccessToken(1L);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(s3Service.createPreSignedURL(
            eq(FileType.COVER_LETTER), eq(fileName), eq(userId), eq(interviewId), isNull()))
        .thenReturn(expectedUrl);

    // When & Then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/cover-letter/{interviewId}/{fileName}/upload-url", interviewId, fileName)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // Validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedUrl));

    // REST Docs
    resultActions.andDo(
        document(
            "면접 정보 입력 시 PreSigned URL 생성",
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("interviewId").description("면접 ID"),
                parameterWithName("fileName").description("업로드할 파일명")),
            responseBody()));
  }

  @Test
  @DisplayName("마이페이지에서 PreSigned URL 생성")
  @WithMockUser
  void testGetCoverLetterUploadUrlOnMyPage() throws Exception {
    // Given
    Long userId = 1L;
    String fileName = "testFile.txt";
    String expectedUrl = "https://s3.aws.com/testFile.txt";

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(s3Service.createPreSignedURL(
            eq(FileType.COVER_LETTER), eq(fileName), eq(userId), isNull(), isNull()))
        .thenReturn(expectedUrl);

    // When & Then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/cover-letter/{fileName}/upload-url", fileName)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // Validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedUrl));

    // REST Docs
    resultActions.andDo(
        document(
            "마이페이지에서 PreSigned URL 생성",
            preprocessResponse(prettyPrint()),
            pathParameters(parameterWithName("fileName").description("업로드할 파일명")),
            responseBody()));
  }
}
