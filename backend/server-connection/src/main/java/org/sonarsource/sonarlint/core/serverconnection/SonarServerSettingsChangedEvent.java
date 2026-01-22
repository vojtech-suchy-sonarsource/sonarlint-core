/*
ACR-873d7726e2a440089eb17cabb5d736bc
ACR-33ebaca1076e4e98b7ba6a165daa6801
ACR-f5755122c11f4a34a89b37d66ec77bbe
ACR-2e56ce6ec0ee4ce88b7f88f8c626b3ec
ACR-551a7601f24e48d1b919e2e3340e635c
ACR-e0da619a1b364fb3868e7542bd178f52
ACR-ea80abf555624eef9641d030d1f8895a
ACR-0eb59dbfe1d0443da67c72942356def9
ACR-5c025c5954884a10a27a42c74b3853a2
ACR-87aa6e2bb27044ce8960f07ac52726b4
ACR-5fdfccff8f8e4694a1a2526cd688025c
ACR-da9d2a464cb04210809b86dc48a26cbc
ACR-3ab4a57aebb74da5a7bf715ba8e59afe
ACR-8dfd6f65834546f7bfb263d69c4e091b
ACR-6c908c6fc4ad49048c7587519c0b7b44
ACR-0dac12734b274d1f9ec97db5d9d64103
ACR-d975e86cea2940b4bc4187e40773f9fd
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Map;
import java.util.Set;

public class SonarServerSettingsChangedEvent {

  private final Set<String> configScopeIds;
  private final Map<String, String> updatedSettingsValueByKey;

  public SonarServerSettingsChangedEvent(Set<String> configScopeIds, Map<String, String> updatedSettingsValueByKey) {
    this.configScopeIds = configScopeIds;
    this.updatedSettingsValueByKey = updatedSettingsValueByKey;
  }

  public Set<String> getConfigScopeIds() {
    return configScopeIds;
  }

  public Map<String, String> getUpdatedSettingsValueByKey() {
    return updatedSettingsValueByKey;
  }
}
