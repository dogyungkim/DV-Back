package org.richardstallman.dvback.global.oauth;

import java.util.Map;
import lombok.Getter;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.entity.UserEntity;

@Getter
public class OAuthAttributes {

  private final String nameAttributeKey;
  private final KakaoUserInfo kakaoUserInfo;
  UserConverter userConverter;

  private OAuthAttributes(String nameAttributeKey, KakaoUserInfo kakaoUserInfo) {
    this.nameAttributeKey = nameAttributeKey;
    this.kakaoUserInfo = kakaoUserInfo;
  }

  public static OAuthAttributes of(String userNameAttributeName, Map<String, Object> attributes) {
    return ofKakao(userNameAttributeName, attributes);
  }

  private static OAuthAttributes ofKakao(
      String userNameAttributeName, Map<String, Object> attributes) {
    return new OAuthAttributes(userNameAttributeName, new KakaoUserInfo(attributes));
  }

  public UserEntity toEntity() {
    return userConverter.kakaoInfoToUserEntity(kakaoUserInfo);
  }
}
