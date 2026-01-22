/*
ACR-db15a257348f4805a0b29e22c052f7b8
ACR-88f0a69d54fb458ea0ba5f03511e120e
ACR-8247cc159eb847439ee47c1e413c4593
ACR-91d769a21d2944dba3d9eb6ceb1369fb
ACR-9921207fff32489ea6f1a86104cbdf0a
ACR-e99a0eece8184bb4a34d33fb738ac386
ACR-6961f31510e94135b8d4a015016af990
ACR-ef96ab35dd7f434d9818b7c7cd6eafeb
ACR-46536d3c47ae4af3b77938c306912b21
ACR-f3ee8b8357fd4e56bf542c9736f296aa
ACR-5a0c3eeb4b8d430daccdce03dfab0e8e
ACR-87281caf05174355bd73d6e7f51f6901
ACR-2d38f898a09d4649a7bdb5decce252ad
ACR-bfc1dd9c41014ce5b6ee00caa8ab38bc
ACR-96955ebe0c91470e80a5b249ea2662fa
ACR-0afa6d7a748844999dd16b172cbc9159
ACR-d3b3ed1e061f4fd19ca24c54b32cd657
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
