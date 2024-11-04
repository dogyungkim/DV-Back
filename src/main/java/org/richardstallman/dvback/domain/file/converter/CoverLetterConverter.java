package org.richardstallman.dvback.domain.file.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.entity.CoverLetterEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoverLetterConverter {

  public CoverLetterEntity fromDomainToEntity(CoverLetterDomain coverLetterDomain) {
    return new CoverLetterEntity(
        coverLetterDomain.getCoverLetterId(),
        coverLetterDomain.getUserId(),
        coverLetterDomain.getFileName(),
        coverLetterDomain.getS3FileUrl());
  }

  public CoverLetterDomain fromEntityToDomain(CoverLetterEntity coverLetterEntity) {
    return CoverLetterDomain.builder()
        .coverLetterId(coverLetterEntity.getCoverLetterId())
        .userId(coverLetterEntity.getUserId())
        .fileName(coverLetterEntity.getFileName())
        .s3FileUrl(coverLetterEntity.getS3FileUrl())
        .build();
  }

  public CoverLetterDomain fromUrlToDomain(String coverLetterUrl, Long userId, String fileName) {
    return CoverLetterDomain.builder()
        .userId(userId)
        .fileName(fileName)
        .s3FileUrl(coverLetterUrl)
        .build();
  }
}
