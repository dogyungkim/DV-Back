package org.richardstallman.dvback.domain.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterListResponseDto;
import org.richardstallman.dvback.domain.file.service.CoverLetterService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/file", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {

  private final S3Service s3Service;
  private final CoverLetterService coverLetterService;

  @GetMapping("/cover-letter/{interviewId}/{fileName}/upload-url")
  public ResponseEntity<String> getCoverLetterUploadUrlWhenInputInterviewInfo(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long interviewId,
      @PathVariable String fileName) {
    log.info(
        "Generating preSigned URL for file upload: interviewId={}, fileName={}",
        interviewId,
        fileName);

    String preSignedUrl =
        s3Service.createPreSignedURL(FileType.COVER_LETTER, fileName, userId, interviewId, null);

    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(preSignedUrl);
  }

  @GetMapping("/cover-letter/{fileName}/upload-url")
  public ResponseEntity<String> getCoverLetterUploadUrlOnMyPage(
      @AuthenticationPrincipal Long userId, @PathVariable String fileName) {
    log.info("Generating preSigned URL for file upload: fileName={}", fileName);

    String preSignedUrl =
        s3Service.createPreSignedURL(FileType.COVER_LETTER, fileName, userId, null, null);

    return ResponseEntity.ok(preSignedUrl);
  }

  @GetMapping("/cover-letter")
  public ResponseEntity<DvApiResponse<CoverLetterListResponseDto>> getUserCoverLetterList(
      @AuthenticationPrincipal Long userId) {
    log.info("Get Cover Letter List for User=({})", userId);

    CoverLetterListResponseDto coverLetterListResponseDto =
        new CoverLetterListResponseDto(coverLetterService.findCoverLettersByUserId(userId));
    return ResponseEntity.ok(DvApiResponse.of(coverLetterListResponseDto));
  }
}
