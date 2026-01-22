/*
ACR-702d8afdfcd4405f900236f3b86048a6
ACR-878a3fb7139d48089f7162764d98f1b2
ACR-fe85a5209eed4a85a44b994b29bfa336
ACR-6d26d2e5bc2e4631979d49da88274d11
ACR-e498c04ed36c46adb04ce55012225b16
ACR-e5be6d84b3e8486bad2259b8c46a804d
ACR-4182412662d34ac090291433eb95bef1
ACR-0a6ef68faf11404ab8a220d38171eb5b
ACR-73b4485ddf6b44fb8b9e2a00ecf20c1d
ACR-41aaebd1742c45d3b71ea63964c4932c
ACR-54bd5d199f4c443b9025d99c6bab4e3a
ACR-cce5450abcac4ca3b8f84895e334c9bf
ACR-d88ac62a9afa4a6abf5c943154ce09d5
ACR-e0a754bf04894154b4f27c4b44b65433
ACR-ad0b6c620fab40b78c69d17c619d016f
ACR-19b9281b37ba46a1be6986820089385f
ACR-d73a355ceecb45dbb8133aa92de855b3
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix;

import java.util.List;
import java.util.UUID;

public class SuggestFixResponse {
  private final UUID id;
  private final String explanation;
  private final List<SuggestFixChangeDto> changes;

  public SuggestFixResponse(UUID id, String explanation, List<SuggestFixChangeDto> changes) {
    this.id = id;
    this.explanation = explanation;
    this.changes = changes;
  }

  public UUID getId() {
    return id;
  }

  public String getExplanation() {
    return explanation;
  }

  public List<SuggestFixChangeDto> getChanges() {
    return changes;
  }
}
