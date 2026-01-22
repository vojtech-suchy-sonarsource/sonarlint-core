/*
ACR-033f90d9e9054518871d818a6ccbc776
ACR-ec7201356e9b4dc7a82a3af494fab3e6
ACR-56effffe31ee4932949b998200181f80
ACR-2bf73a2a609f4acfa322cc9b6296dd97
ACR-bea3a78f8d33473a92e22f0a9f4d871b
ACR-7f28818de0084321ab6da593c3ebaf3e
ACR-7ccbf5b7b5d74f68bea54e25cd51045e
ACR-682708749fc342aa8e3fc8ff073db010
ACR-15d4829a1ce7445594157dc3781df3c0
ACR-0b8efe8a49b942b4bd29f2128ce89a5d
ACR-364c1eb33183410d92750690f8a74374
ACR-ab98262875404692b41894ca3a5675c0
ACR-65a7f410c07046968fe92c83a7dfe819
ACR-ea5248b48651415dbe19b75e84984957
ACR-816a1664f5854a5abb578abaf5bc0a1d
ACR-05a2f5404b7c45daadc3f0e2d6dcf0da
ACR-142e071ee1cf405eb074843e6fa271eb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class RuleParamDefinitionDto {
  private final String key;
  private final String name;
  private final String description;
  private final String defaultValue;
  private final RuleParamType type;
  private final boolean multiple;
  private final List<String> possibleValues;

  public RuleParamDefinitionDto(String key, String name, String description, @Nullable String defaultValue, RuleParamType type, boolean multiple, List<String> possibleValues) {
    this.key = key;
    this.name = name;
    this.description = description;
    this.defaultValue = defaultValue;
    this.type = type;
    this.multiple = multiple;
    this.possibleValues = possibleValues;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  @CheckForNull
  public String getDefaultValue() {
    return defaultValue;
  }

  public RuleParamType getType() {
    return type;
  }

  public boolean isMultiple() {
    return multiple;
  }

  public List<String> getPossibleValues() {
    return possibleValues;
  }
}
