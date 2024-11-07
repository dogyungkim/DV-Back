package org.richardstallman.dvback.domain.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterListResponseDto;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;
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
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>>
      getCoverLetterUploadUrlWhenInputInterviewInfo(
          @AuthenticationPrincipal Long userId,
          @PathVariable Long interviewId,
          @PathVariable String fileName) {
    log.info(
        "Generating preSigned URL for file upload: interviewId={}, fileName={}",
        interviewId,
        fileName);

    PreSignedUrlResponseDto preSignedUrl =
        s3Service.createPreSignedURLForInterview(
            FileType.COVER_LETTER, fileName, userId, interviewId, null);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(DvApiResponse.of(preSignedUrl));
  }

  @GetMapping("/cover-letter/{fileName}/upload-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getCoverLetterUploadUrlOnMyPage(
      @AuthenticationPrincipal Long userId, @PathVariable String fileName) {
    log.info("Generating preSigned URL for file upload: fileName={}", fileName);

    PreSignedUrlResponseDto preSignedUrl =
        s3Service.createPreSignedURLForInterview(
            FileType.COVER_LETTER, fileName, userId, null, null);

    return ResponseEntity.ok(DvApiResponse.of(preSignedUrl));
  }

  @GetMapping("/cover-letter/{interviewId}/download-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getCoverLetterDownloadUrlOnMyPage(
      @AuthenticationPrincipal Long userId, @PathVariable Long interviewId) {
    log.info("Generating preSigned URL for file download: interviewId={}", interviewId);

    PreSignedUrlResponseDto preSignedUrlResponseDto =
        s3Service.getDownloadURLForInterview(
            coverLetterService.findCoverLetterByInterviewId(interviewId).getS3FileUrl(),
            userId,
            interviewId);

    return ResponseEntity.ok(DvApiResponse.of(preSignedUrlResponseDto));
  }

  @GetMapping("/profile-image/{fileName}/upload-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getProfileImageUploadUrl(
      @AuthenticationPrincipal Long userId, @PathVariable String fileName) {
    log.info("Generating preSigned URL for profile image upload: userId={}", userId);

    PreSignedUrlResponseDto preSignedUrl = s3Service.getPreSignedUrlForImage(fileName, userId);

    return ResponseEntity.ok(DvApiResponse.of(preSignedUrl));
  }

  @GetMapping("/profile-image/download-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getProfileImageDownloadUrl(
      @AuthenticationPrincipal Long userId) {
    log.info("Generating preSigned URL for profile image upload: userId={}", userId);

    PreSignedUrlResponseDto preSignedUrlResponseDto = s3Service.getDownloadUrlForImage(userId);
    return ResponseEntity.ok(DvApiResponse.of(preSignedUrlResponseDto));
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
