/*
ACR-3f864f8fbb07446aac4a24aa64629a50
ACR-22a6205ca2d849a99490285d8258a28d
ACR-2eaf4e08e9e54c37a708430843beb7d2
ACR-eef045f8d00047f7b03f3daf4fab4277
ACR-16310ac33a6343559fe39eced9d7e4d8
ACR-5e9364703e374a75a0eed1474325fb5d
ACR-62960f921d754ca9895275760713344d
ACR-a0a3193c88544d7e9a901203479d66d9
ACR-d6f40f354537421cbd39a07594e5584a
ACR-55b5eb0f56ed451ab43154c3de65aa97
ACR-fcb5c8c3161e443ab35184e9e01a2809
ACR-26ac5312225349498b46764a8d89a1dd
ACR-407d62e6dff84885ad89767e08f67bd4
ACR-925156a7c45f48b59cc6b20340909b02
ACR-35d7b388bd034a328e547bc3abc86511
ACR-45391c984663428399135e448aa41c9c
ACR-2e90e800161b41709ade9498f75a7562
 */
package org.sonarsource.sonarlint.core.serverapi.qualityprofile;

public class QualityProfile {
  private final boolean isDefault;
  private final String key;
  private final String name;
  private final String language;
  private final String languageName;
  private final long activeRuleCount;
  private final String rulesUpdatedAt;
  private final String userUpdatedAt;

  public QualityProfile(boolean isDefault, String key, String name, String language, String languageName,
    long activeRuleCount, String rulesUpdatedAt, String userUpdatedAt) {
    this.isDefault = isDefault;
    this.key = key;
    this.name = name;
    this.language = language;
    this.languageName = languageName;
    this.activeRuleCount = activeRuleCount;
    this.rulesUpdatedAt = rulesUpdatedAt;
    this.userUpdatedAt = userUpdatedAt;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getLanguage() {
    return language;
  }

  public String getLanguageName() {
    return languageName;
  }

  public long getActiveRuleCount() {
    return activeRuleCount;
  }

  public String getRulesUpdatedAt() {
    return rulesUpdatedAt;
  }

  public String getUserUpdatedAt() {
    return userUpdatedAt;
  }
}
