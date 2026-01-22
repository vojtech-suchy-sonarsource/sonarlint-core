/*
ACR-2864b5fa7d97495a8dcd3ba64486675c
ACR-655adfef3203489582645989c62d5bd8
ACR-a5c92bc347f04022b54e326be8ee9244
ACR-4d8e4bde58454a8180def3805ba15d59
ACR-9d51776ea53c4531b562fbd043f4df1b
ACR-1420d1dddc05402fb418b31350c770b1
ACR-ba0a930e91dd4318a5f9b84eb28a606d
ACR-e0b9ff52549d47c297748987f5a4c76e
ACR-329c0497f9e64b83a1716db4908ca90a
ACR-3d0e633182414478813c053c496e37c6
ACR-5449600c3e9b411d95738c1b57f8151f
ACR-05f20f6f48e34a26a57bd1c7783af94d
ACR-5267126cd5294d24af54a24f53826e49
ACR-4bb0153035504333863a2f99a49512dc
ACR-2c4f8fc352014d5b8fc27ff65b673331
ACR-65ea009bc580485e92acfdbcfd98637b
ACR-5b08723c9da1452080844966ee58dc39
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
