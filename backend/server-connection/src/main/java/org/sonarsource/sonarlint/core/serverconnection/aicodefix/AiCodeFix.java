/*
ACR-64d0a175641a47b2acbc7184eea113fc
ACR-6936f75b2b7b4bdcb20cb574c08c4596
ACR-bf013f7e92524336a4baa86e4ba190ee
ACR-c9a4c8d8eca1449595d8ad834e28aa09
ACR-29347fc649d34718a3d37c81c0945c1e
ACR-e6fa498bf574473e924e5c28be7db8bf
ACR-4cb95cec53c54bb1b74b39cf476336b3
ACR-f52f7912806e45df8f6d66f821c0400a
ACR-f82dc5cc278645aeaa4e3206e90c030a
ACR-efce75e185194de9aac1f3c112ea5003
ACR-64c948da1c4b4fe08694110586264fab
ACR-e8b0be6f52d4454bb87ec33cd62e4c7a
ACR-50c39e1446c54e24b89834eb691ab858
ACR-bcd5ee2d963d45bfabc8ea74f4083a60
ACR-cbfcf6d342da452180969e21f8de49bc
ACR-83a9eda5f95b4b68a6926b4179f6c08e
ACR-b3d66433e5a848379f9174de92cbe74d
 */
package org.sonarsource.sonarlint.core.serverconnection.aicodefix;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/*ACR-9e02341183bc4369a2b8a8c8e425ec72
ACR-4a6426c4c1334d02837b5ea0b304b14e
ACR-3984db8395984613a6d1293909e9dcbd
ACR-282861a00294456f9a1049af8a15979d
 */
public record AiCodeFix(
  String connectionId,
  String[] supportedRules,
  boolean organizationEligible,
  Enablement enablement,
  String[] enabledProjectKeys
) {

  public AiCodeFix(String connectionId, Collection<String> supportedRules, boolean organizationEligible, Enablement enablement, Collection<String> enabledProjectKeys) {
    this(connectionId, supportedRules.toArray(String[]::new), organizationEligible, enablement, enabledProjectKeys.toArray(String[]::new));
  }

  public enum Enablement {
    DISABLED,
    ENABLED_FOR_ALL_PROJECTS,
    ENABLED_FOR_SOME_PROJECTS
  }

  public AiCodeFix {
    Objects.requireNonNull(connectionId, "connectionId");
    Objects.requireNonNull(supportedRules, "supportedRules");
    Objects.requireNonNull(enablement, "enablement");
    Objects.requireNonNull(enabledProjectKeys, "enabledProjectKeys");
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    var aiCodeFix = (AiCodeFix) o;
    return organizationEligible == aiCodeFix.organizationEligible && Objects.equals(connectionId, aiCodeFix.connectionId) && enablement == aiCodeFix.enablement
      && Objects.deepEquals(supportedRules, aiCodeFix.supportedRules) && Objects.deepEquals(enabledProjectKeys, aiCodeFix.enabledProjectKeys);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectionId, Arrays.hashCode(supportedRules), organizationEligible, enablement, Arrays.hashCode(enabledProjectKeys));
  }

  @Override
  public String toString() {
    return "AiCodeFix{" +
      "connectionId='" + connectionId + '\'' +
      ", supportedRules=" + Arrays.toString(supportedRules) +
      ", organizationEligible=" + organizationEligible +
      ", enablement=" + enablement +
      ", enabledProjectKeys=" + Arrays.toString(enabledProjectKeys) +
      '}';
  }
}
