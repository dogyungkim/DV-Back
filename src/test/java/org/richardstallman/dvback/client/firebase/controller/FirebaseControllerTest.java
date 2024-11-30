package org.richardstallman.dvback.client.firebase.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.richardstallman.dvback.client.firebase.domain.FcmSendDto;
import org.richardstallman.dvback.client.firebase.service.FcmTokenService;
import org.richardstallman.dvback.client.firebase.service.FirebaseMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WithMockUser(username = "testUser")
@ActiveProfiles("test")
public class FirebaseControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private FirebaseMessagingService firebaseMessagingService;

  @MockBean private FcmTokenService fcmTokenService;

  @Test
  @DisplayName("테스트용 알림 전송 테스트 - 성공")
  void test_send_notification() throws Exception {
    FcmSendDto fcmSendDto = new FcmSendDto("Test Title", "Test Body");

    Mockito.doNothing()
        .when(firebaseMessagingService)
        .sendNotification(any(Long.class), eq(fcmSendDto.title()), eq(fcmSendDto.body()));

    mockMvc
        .perform(
            post("/fcm/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fcmSendDto)))
        .andExpect(status().isOk())
        .andExpect(content().string("Notification sent successfully!"));
  }

  @Test
  @DisplayName("FCM 토큰 저장 테스트 - 성공")
  void save_fcm_token() throws Exception {
    Map<String, String> token = Map.of("token", "sampleToken");

    Mockito.doNothing().when(fcmTokenService).createFcmToken(any(Long.class), eq("sampleToken"));

    ResultActions resultActions =
        mockMvc
            .perform(
                post("/fcm/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(token)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("data.message").value("Token saved successfully!"));

    // restdocs
    resultActions.andDo(
        document(
            "FCM 토큰 저장 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("FCM API")
                    .summary("Firebase Cloud Messaging API")
                    .description("유저의 FCM 토큰을 저장하는 API")
                    .requestFields(
                        fieldWithPath("token").type(JsonFieldType.STRING).description("유저의 FCM 토큰"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.message")
                            .type(JsonFieldType.STRING)
                            .description("처리 결과 메시지"))
                    .build())));
  }

  @Test
  @DisplayName("FCM 토큰 삭제 - 성공")
  void delete_fcm_token() throws Exception {
    Map<String, String> token = Map.of("token", "sampleToken");

    Mockito.doNothing().when(fcmTokenService).deleteFcmToken(any(Long.class), eq("sampleToken"));

    mockMvc
        .perform(
            delete("/fcm/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(token)))
        .andExpect(status().isOk())
        .andExpect(content().string("Token removed successfully!"));
  }
}
