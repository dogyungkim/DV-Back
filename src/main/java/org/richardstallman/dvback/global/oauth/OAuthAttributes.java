package org.richardstallman.dvback.global.oauth;

import java.util.Map;
import lombok.Getter;
import org.richardstallman.dvback.domain.user.entity.UserEntity;

@Getter
public class OAuthAttributes {

  private final String nameAttributeKey;
  private final KakaoUserInfo kakaoUserInfo;


  private OAuthAttributes(String nameAttributeKey, KakaoUserInfo kakaoUserInfo) {
    this.nameAttributeKey = nameAttributeKey;
    this.kakaoUserInfo = kakaoUserInfo;
  }

  public static OAuthAttributes of(String userNameAttributeName, Map<String, Object> attributes) {
    return ofKakao(userNameAttributeName, attributes);
  }

  private static OAuthAttributes ofKakao(String userNameAttributeName,
      Map<String, Object> attributes) {
    return new OAuthAttributes(userNameAttributeName, new KakaoUserInfo(attributes));
  }

  public UserEntity toEntity() {
    return UserEntity.of(
        kakaoUserInfo.getId(),
        kakaoUserInfo.getNickname(),
        kakaoUserInfo.getNickname(),
        kakaoUserInfo.getProfileImage()
    );
  }
}
