/*
ACR-975eadaa62eb49308ec1369935e373c9
ACR-cc53249a76f7418b832306c10753889a
ACR-4c8301dbc672426c99a347153c8d8541
ACR-28c1ee1bdbe843729492e0aafea2e95b
ACR-e51f6c57e9cf45c2b003a7772fcefe9a
ACR-78df25df6e36446186c81a08581169da
ACR-55575de24e5a43c9b86b36aafa0e432c
ACR-fec63c16eb2e4e8f9d1c940b6679bd38
ACR-56a1b044c6f649b9b077f3bf16cf1d72
ACR-f3a946b3577f4e5c9e8b5a43df3e7fef
ACR-f63bdc8dbac6424da583b0062725e954
ACR-19b28cbf510042d098797957c1c1c19b
ACR-73e2e1658d9646368bc0eeeadbd468aa
ACR-2b1a8fc13d01446a95f6aaf2e4282625
ACR-b0115814b0bb45238a4f769820b69d60
ACR-703adec4dfd6419d8598ebdfd0f7da7c
ACR-e15af77ae8ab4637ad000fa2682dde82
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class EffectiveRuleParamDto {
  private final String name;
  private final String description;
  private final String value;
  private final String defaultValue;

  public EffectiveRuleParamDto(String name, String description, @Nullable String value, @Nullable String defaultValue) {
    this.name = name;
    this.description = description;
    this.value = value;
    this.defaultValue = defaultValue;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  /*ACR-fa099ad9e1844475a2e5878a37c1b447
ACR-b56818422f59418f9c0e146643a0b12e
   */
  @CheckForNull
  public String getValue() {
    return value;
  }

  /*ACR-c13d9e1db55d47d1891749dc5f2a2836
ACR-06fcd49e5e4641839e5f7c00075ac037
   */
  @CheckForNull
  public String getDefaultValue() {
    return defaultValue;
  }
}
