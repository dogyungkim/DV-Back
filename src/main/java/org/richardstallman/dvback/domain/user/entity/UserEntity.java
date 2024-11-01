package org.richardstallman.dvback.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String socialId;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String nickname;

  @Column(name = "profile_image")
  private String profileImage;


  private UserEntity(final String socialId, final String email, final String name,
      final String nickname,
      final String profileImage) {
    this.socialId = socialId;
    this.email = email;
    this.name = name;
    this.nickname = nickname;
    this.profileImage = profileImage;
  }

  public static UserEntity of(String socialId, String email, String name, String nickname,
      String profileImage) {
    return new UserEntity(socialId, email, name, nickname, profileImage);
  }
}
