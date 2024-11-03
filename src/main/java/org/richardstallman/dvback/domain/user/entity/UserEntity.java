package org.richardstallman.dvback.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
  private Long id;

  @Column(nullable = false, unique = true)
  private String socialId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String nickname;

  @Column(name = "profile_image")
  private String profileImage;

  public  UserEntity(String socialId, String name,
      String nickname,
      String profileImage) {
    super();
    this.socialId = socialId;
    this.name = name;
    this.nickname = nickname;
    this.profileImage = profileImage;
  }

  public static UserEntity of(String socialId, String name, String nickname,
      String profileImage) {
    return new UserEntity(socialId, name, nickname, profileImage);
  }
}
