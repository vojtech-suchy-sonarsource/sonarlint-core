/*
ACR-e82671304c9a4da5bad20b8b23ebfd0a
ACR-61dc14ddc22b4b288c429f47d639ea05
ACR-8442b84a73be4e43aeb61d8184446ec6
ACR-6f95569f07164cceb1c78526e3a9d670
ACR-d5e16ee37bbb438e9828ccf50b567169
ACR-1f017b47ea6d478d95c05c2689101161
ACR-9a54a5239aab490c9dd2771ec795dedc
ACR-db8a2e8d39f848a0af75b39e32d359e9
ACR-0453db1516df44f1b55a5772fa4da9d6
ACR-ce709cb4f4b14760888912fcd24af163
ACR-c48f3dd9bc444eb19d788e81a7b66803
ACR-61f66c4a40d34301a597f348b66b9964
ACR-2667b23e2a214694ad70ad643fa8d661
ACR-124d9603d94147b99d9c645a8e29218b
ACR-20aed1a5fcef4def89e89d38e8c34cfe
ACR-2ed20451c44541b3a30d3a5aac2a07e0
ACR-4eeee494d4b7497e941fcfd6db5213b5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

import javax.annotation.Nullable;

public class FixSuggestionResolvedParams {
  private final String suggestionId;
  private final FixSuggestionStatus status;
  @Nullable
  private final Integer snippetIndex;

  public FixSuggestionResolvedParams(String suggestionId, FixSuggestionStatus status, @Nullable Integer snippetIndex) {
    this.suggestionId = suggestionId;
    this.status = status;
    this.snippetIndex = snippetIndex;
  }

  public String getSuggestionId() {
    return suggestionId;
  }

  public FixSuggestionStatus getStatus() {
    return status;
  }

  @Nullable
  public Integer getSnippetIndex() {
    return snippetIndex;
  }
}
