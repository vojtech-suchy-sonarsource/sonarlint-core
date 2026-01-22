/*
ACR-c5e94cfa174444359f4c25da87ca1b9b
ACR-b32ef39a6bd4409d929efcc0a69c8dcd
ACR-012d890dc1f9485088f8a7e267aae7ef
ACR-d705293bcbc2468bbfab8679ffd4ab14
ACR-fe9a1e324ea743cba2a855395527b501
ACR-ddfdcf0119c14a53819fb7fef0beb7a2
ACR-4ba93c3a54a84a6d8ed758b5f7b3ea44
ACR-37ae3a9d5d89401d8b5e9eda003b9fa8
ACR-eb6e635d688b4612b67300cad03dd98e
ACR-2d3d0eb5bc3a4b86aa3f490317be873e
ACR-fb22782a3a1e43a7981d4dbeb43810b9
ACR-88255dbd36b14d7b9bb39c0e75e48a07
ACR-57f963da68eb499abe9a0ddc52732bbc
ACR-84853c54e8e74656b855fcc4487e0a8d
ACR-3a766770accc48b49d4ea2349ad5eae6
ACR-097bd85af358442484d41fadfc122495
ACR-67841f4e96c446bc8db5e8c648ddba24
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
