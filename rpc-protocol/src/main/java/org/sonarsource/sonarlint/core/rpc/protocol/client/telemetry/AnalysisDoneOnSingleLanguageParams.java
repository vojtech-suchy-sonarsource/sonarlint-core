/*
ACR-010d1f85b2ce454e9be6600a1e09f792
ACR-73668a4bd92e4e35ac3a3b1d0134a479
ACR-d18bbfd2db8746f888c6ec7cffb60099
ACR-56474a68009e49039ea6e0812ad5f8d9
ACR-0640684ed27f43558ebf0aa0f5c4f3b3
ACR-b9139929dbf44ff2a33c17513258c15a
ACR-c0d442f7543a493c95a1832079827d63
ACR-c219413fb768422eae44bb6a61ba7d06
ACR-64618a07253d4a96b2cbcc7d59947b76
ACR-cf808205606f41b6bbc131eefdfd183d
ACR-881d68f7900b416b853424aca02e750d
ACR-7c4519c730644b9c9bf2178ea4268e0c
ACR-b10d5cc136c147e0b64fc087f76a5b0e
ACR-7d68d50766344fd89cd2a94956a27daf
ACR-5440fc74099b4bdd98b3ea7827c96385
ACR-8a4ceb311bce4e2b9c516521ca1df641
ACR-9fe0703a8e3f479cbcd9359615d0f133
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class AnalysisDoneOnSingleLanguageParams {
  @Nullable
  private final Language language;
  private final int analysisTimeMs;

  public AnalysisDoneOnSingleLanguageParams(@Nullable Language language, int analysisTimeMs) {
    this.language = language;
    this.analysisTimeMs = analysisTimeMs;
  }

  @Nullable
  public Language getLanguage() {
    return language;
  }

  public int getAnalysisTimeMs() {
    return analysisTimeMs;
  }
}
