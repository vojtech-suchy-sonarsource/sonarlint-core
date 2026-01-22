/*
ACR-46c62a47a0fc4f218bb3b60ab2d460a1
ACR-2534255c97f84d9dada9a67a313c43a4
ACR-2399ad2d979a4bbdab3b9381cf161da2
ACR-098d23f350e44ee6840d7b5d6046c2cf
ACR-b27c93797dfb418dbc1be6a152aa006c
ACR-c8840707aa9c472198b3eab99822374a
ACR-59c093b26d3e4592881f99353d5943fe
ACR-3e1e93ab0d2b4cc987c2ced0e4cf5370
ACR-d6ab95e354db4a619851f8adc8dce6e7
ACR-e324f92b052d45438feea04e3104d336
ACR-5f6d996dfe084a47bd277f1c7989d2fd
ACR-fd1249f9bf2541fbaf56c02c69231363
ACR-96b6006a1a83434a848fe186ed8df8b1
ACR-fc9c0a1e6b594df69bd528bd271193e9
ACR-da689f5c53be49ffa70c92dbec8f4967
ACR-c9601e98b0d847ab90db9a91e9f45924
ACR-0a0791f8032b42f38db9d503bbfbadc9
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
