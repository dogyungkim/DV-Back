package org.richardstallman.dvback.global.oauth;

import java.util.Map;

public class KakaoUserInfo {

  private final Map<String, Object> attributes;

  public KakaoUserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public String getId() {
    return String.valueOf(attributes.get("id"));
  }

  public String getEmail() {
    Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
    return (String) account.get("email");
  }

  public String getNickname() {
    Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) account.get("profile");
    return (String) profile.get("nickname");
  }

  public String getProfileImage() {
    Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) account.get("profile");
    return (String) profile.get("thumbnail_image_url");
  }
}