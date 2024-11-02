package org.richardstallman.dvback.domain.file.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cover_letters")
public class CoverLetterEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cover_letter_seq")
  @SequenceGenerator(
      name = "cover_letter_seq",
      sequenceName = "cover_letter_id_seq",
      allocationSize = 1)
  private Long coverLetterId;

  // 추후 entity로 변경
  private Long userId;

  @NotNull(message = "Interview Type is required") private String fileName;

  @NotNull(message = "S3 File Url is required") private String s3FileUrl;
}
