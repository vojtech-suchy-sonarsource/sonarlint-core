/*
ACR-4c07379398a14a7c898bff254f299ca7
ACR-a999f30f1ed4462e946fb582772f2701
ACR-d505daefa93f404b9ad16aa817508d1f
ACR-f0c637b74e444e23ba169516bd11feb0
ACR-aee8b1c094ab4b17b6b8524042dfe42d
ACR-99059b4d4a814273b15382b3495b20f0
ACR-0e54d776edfb49f49c4a189ee2e15022
ACR-b2df4da2916c4bf9ab309eaa13fdca3f
ACR-03344e280b5446b5a86d1f074b630768
ACR-1756e2629fa54b16a8c9d2edbf37592e
ACR-1a4cbb4d236f48af80f398a9eedf4545
ACR-ce739c6f798a4b9f81884dc4b0ba0f0a
ACR-c659b736da0349cfb4ab94918b6de216
ACR-013281747c7447b68f74cb42ed90f1f5
ACR-d550d4029a7044eb961f7aa9e82a25bf
ACR-3942bb053cdd4c018e83a08b381a524a
ACR-7ce1fba04c754576872ebc3929fc725c
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
