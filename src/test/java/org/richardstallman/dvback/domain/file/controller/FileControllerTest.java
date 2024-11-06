package org.richardstallman.dvback.domain.file.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;
import org.richardstallman.dvback.domain.file.service.CoverLetterService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.restdocs.payload.JsonFieldType;
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
  @MockBean private CoverLetterService coverLetterService;

  @Test
  @DisplayName("면접 정보 입력 시 PreSigned URL 생성")
  @WithMockUser
  void testGetCoverLetterUploadUrlWhenInputInterviewInfo() throws Exception {
    // Given
    Long userId = 1L;
    Long interviewId = 2L;
    String fileName = "testFile.txt";
    String expectedUrl = "https://s3.aws.com/testFile.txt";
    PreSignedUrlResponseDto preSignedUrlResponseDto = new PreSignedUrlResponseDto(expectedUrl);

    String accessToken = jwtUtil.generateAccessToken(1L);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(s3Service.createPreSignedURL(
            eq(FileType.COVER_LETTER), eq(fileName), eq(userId), eq(interviewId), isNull()))
        .thenReturn(preSignedUrlResponseDto);

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
        .andExpect(jsonPath("data.preSignedUrl").value(expectedUrl));

    // REST Docs
    resultActions.andDo(
        document(
            "면접 정보 입력 시 PreSigned URL 생성",
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("interviewId").description("면접 ID"),
                parameterWithName("fileName").description("업로드할 파일명")),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data.preSignedUrl")
                    .type(JsonFieldType.STRING)
                    .description("aws s3 preSigned Url"))));
  }

  @Test
  @DisplayName("마이페이지에서 PreSigned URL 생성")
  @WithMockUser
  void testGetCoverLetterUploadUrlOnMyPage() throws Exception {
    // Given
    Long userId = 1L;
    String fileName = "testFile.txt";
    String expectedUrl = "https://s3.aws.com/testFile.txt";
    PreSignedUrlResponseDto preSignedUrlResponseDto = new PreSignedUrlResponseDto(expectedUrl);

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(s3Service.createPreSignedURL(
            eq(FileType.COVER_LETTER), eq(fileName), eq(userId), isNull(), isNull()))
        .thenReturn(preSignedUrlResponseDto);

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
        .andExpect(jsonPath("data.preSignedUrl").value(expectedUrl));

    // REST Docs
    resultActions.andDo(
        document(
            "마이페이지에서 PreSigned URL 생성",
            preprocessResponse(prettyPrint()),
            pathParameters(parameterWithName("fileName").description("업로드할 파일명")),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data.preSignedUrl")
                    .type(JsonFieldType.STRING)
                    .description("aws s3 PreSigned Url"))));
  }

  @Test
  @DisplayName("유저가 올린 자소서 목록 조회")
  @WithMockUser
  void testGetCoverLetterByUserId() throws Exception {
    // Given
    Long userId = 1L;
    List<CoverLetterResponseDto> expectedCoverLetterResponseDtos = new ArrayList<>();
    expectedCoverLetterResponseDtos.add(
        new CoverLetterResponseDto(1L, "testFile.txt", "https://s3.aws.com/testFile.txt"));
    expectedCoverLetterResponseDtos.add(
        new CoverLetterResponseDto(2L, "testFile2.txt", "https://s3.aws.com/testFile2.txt"));
    expectedCoverLetterResponseDtos.add(
        new CoverLetterResponseDto(3L, "testFile3.txt", "https://s3.aws.com/testFile3.txt"));

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(coverLetterService.findCoverLettersByUserId(userId))
        .thenReturn(expectedCoverLetterResponseDtos);

    // When & Then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/cover-letter").cookie(authCookie).accept(MediaType.APPLICATION_JSON));

    // Validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.coverLetters[0].fileId").value(1))
        .andExpect(jsonPath("data.coverLetters[0].type").value("COVER_LETTER"))
        .andExpect(jsonPath("data.coverLetters[0].fileName").value("testFile.txt"))
        .andExpect(
            jsonPath("data.coverLetters[0].s3FileUrl").value("https://s3.aws.com/testFile.txt"))
        .andExpect(jsonPath("data.coverLetters[1].fileId").value(2))
        .andExpect(jsonPath("data.coverLetters[1].type").value("COVER_LETTER"))
        .andExpect(jsonPath("data.coverLetters[1].fileName").value("testFile2.txt"))
        .andExpect(
            jsonPath("data.coverLetters[1].s3FileUrl").value("https://s3.aws.com/testFile2.txt"))
        .andExpect(jsonPath("data.coverLetters[2].fileId").value(3))
        .andExpect(jsonPath("data.coverLetters[2].type").value("COVER_LETTER"))
        .andExpect(jsonPath("data.coverLetters[2].fileName").value("testFile3.txt"))
        .andExpect(
            jsonPath("data.coverLetters[2].s3FileUrl").value("https://s3.aws.com/testFile3.txt"));

    // REST Docs
    resultActions.andDo(
        document(
            "유저가 올린 자소서 목록 조회",
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data.coverLetters[0].fileId")
                    .type(JsonFieldType.NUMBER)
                    .description("자소서 식별자"),
                fieldWithPath("data.coverLetters[0].fileName")
                    .type(JsonFieldType.STRING)
                    .description("자소서 파일 이름"),
                fieldWithPath("data.coverLetters[0].s3FileUrl")
                    .type(JsonFieldType.STRING)
                    .description("자소서 업로드 주소"),
                fieldWithPath("data.coverLetters[0].type")
                    .type(JsonFieldType.STRING)
                    .description("파일 유형: COVER_LETTER"))));
  }
}
