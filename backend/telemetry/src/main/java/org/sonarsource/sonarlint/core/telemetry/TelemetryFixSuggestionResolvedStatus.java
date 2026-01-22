/*
ACR-616ff4f349be42439262854cea6c023c
ACR-97dfd6428cf64820a865f6dadca20a03
ACR-e8dc5ff9c9384a26b37c6f4600c8ae32
ACR-2303f4b3497c402c8bdbabe9aef7e537
ACR-c911b5d0fee2485c8b72638f4c6ed3fd
ACR-b05274b9a2994d20b1134506530391a3
ACR-3364a14aecd549319e2442d661cb31a8
ACR-a647e9a286dd4816b404bdbe19488b86
ACR-2658264e98054f66b23397f1d3a64aab
ACR-3d75bdf542bb441fbd2379368fe5c219
ACR-64d69d41297e4b6a83c89872eebf082a
ACR-39d1b81730824b2f9aa5fb06854e1650
ACR-f1d8999ffee3416ba78db47d865400d8
ACR-b6aa54a4140748b19fbfec0a2c221aa7
ACR-a4b135ac87d04ea8a71b62001dcfa1d1
ACR-94c2ec5dd85e4f57870e4c9e5587d8f3
ACR-330f2a8f68704e9b8482cc89d57e6de7
 */
package org.sonarsource.sonarlint.core.telemetry;

import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FixSuggestionStatus;

public class TelemetryFixSuggestionResolvedStatus {
  @Nullable
  private FixSuggestionStatus fixSuggestionResolvedStatus;
  @Nullable
  private final Integer fixSuggestionResolvedSnippetIndex;

  public TelemetryFixSuggestionResolvedStatus(@Nullable FixSuggestionStatus fixSuggestionResolvedStatus, @Nullable Integer fixSuggestionResolvedSnippetIndex) {
    this.fixSuggestionResolvedStatus = fixSuggestionResolvedStatus;
    this.fixSuggestionResolvedSnippetIndex = fixSuggestionResolvedSnippetIndex;
  }

  public FixSuggestionStatus getFixSuggestionResolvedStatus() {
    return fixSuggestionResolvedStatus;
  }

  @Nullable
  public Integer getFixSuggestionResolvedSnippetIndex() {
    return fixSuggestionResolvedSnippetIndex;
  }

  public void setFixSuggestionResolvedStatus(FixSuggestionStatus fixSuggestionResolvedStatus) {
    this.fixSuggestionResolvedStatus = fixSuggestionResolvedStatus;
  }
}
