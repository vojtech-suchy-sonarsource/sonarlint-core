/*
ACR-6dfdfb2befa54634aabff9acf7041e58
ACR-168c78292fec45fca26f61b9623a5697
ACR-b57370f8fa4b46a8b87d10eb9e99f144
ACR-08c9939f10204a7b9a78c7924bce727a
ACR-8326f9ae3cba4e54841e4031cd395a73
ACR-2d306a2c90c34873a7953ae115c81ace
ACR-c9c6a5b0ab1a4cde9a0b191563d3ff38
ACR-71986e87632248768709130d0b41fb30
ACR-45f2281e6bff4d5f9acd4b9e4d5ac7d1
ACR-1870bbc12d574729abdf330e03c159e2
ACR-406ef3a145054d71ad543d90f10582ce
ACR-234ac257c3644e05ac69caab14acab02
ACR-624fee792c5e4a8d8f3fff483a0c8801
ACR-bc7302dbf9ce4c37881e5628e6b15971
ACR-8bee1df1c5e543d19e2267a5db13061e
ACR-63c59654dca14560b934849a5b93d02c
ACR-71cf73e3ba554c8ea3c20db012de6dde
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
