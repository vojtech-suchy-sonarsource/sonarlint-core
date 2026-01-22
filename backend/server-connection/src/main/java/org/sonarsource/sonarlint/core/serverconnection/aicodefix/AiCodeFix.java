/*
ACR-989087d886c34763acdf679439576ac3
ACR-5d6523f27b20492e9c6cf03bb6d3df9e
ACR-87df26e78afe4cd59bd5395cc5378395
ACR-f7bccf6079e540aaa433ec2de6431a7e
ACR-8fb2fc959a8740bc936fbf968714d6df
ACR-03b4e3a4468d4f61a0175fe5abeab264
ACR-0c750691813b4d588819794dc46fccfa
ACR-4b6fa3cc86c4474690bc0ab6ca78a8b7
ACR-c98c65fe2ee34bc0b7087c1ebf855201
ACR-98635ecdebc9457a9ba1d7a8baa55916
ACR-f75cc1ad96df4f3a9178163e517e2db5
ACR-4475751b9f7640cab8e920d439e7ce96
ACR-080d1b062133471fa2a9976b5142ec0f
ACR-0b8e8484498745d297fec89ed024b407
ACR-6e4eff24a3734ed6b8df9cc7cab85bf5
ACR-566e40710f4c413683c13168a2315efe
ACR-4cdc55fcea5e4722b930eb7c26555849
 */
package org.sonarsource.sonarlint.core.serverconnection.aicodefix;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/*ACR-7fa7ebe67be247fba803f06d9beeb539
ACR-374803cbe3f441659d75c0de89ebf6a4
ACR-39448c6ffbf84df29d86f0e97ef4d62a
ACR-917613a37e84431a9c30c7fefb4f6df2
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
