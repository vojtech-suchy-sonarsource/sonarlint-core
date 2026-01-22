/*
ACR-bec5d0dcc0c849fcb7e436c9112bb1a7
ACR-1f175602ac7f42b189295c5131c9d9ec
ACR-26409f1e88a14776b5a85bb1e66b3283
ACR-4ae711d87bcc460e997c8454a2e88874
ACR-5b7436fa9cf24af6a2752c9e50c22524
ACR-f94b457cacf64a2b95c206140eb0ee2e
ACR-256e0d75bb284d5fbc4efe55004810a6
ACR-19a435b8230446c38668d1480f446140
ACR-5bb99fa191a747788813536984c95d56
ACR-74b0c473529a48afb8674cd70554fcce
ACR-6c9a712b9f1e4b2eaf1c6e1576378a3b
ACR-2c202c23254543a7974fb1997be39b63
ACR-8f37ef60fa18455f80e18fdc11105f6a
ACR-ea46016d00204630a8c91a94f01b1e26
ACR-0f925cc839c44a3baf39ac3538b5315b
ACR-8ee846d5931b4436a228cfd80e477194
ACR-d9ac0b3264b54a5a96237de9545c66ce
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import java.util.List;
import java.util.Map;

public class SuggestConnectionParams {

  private final Map<String, List<ConnectionSuggestionDto>> suggestionsByConfigScopeId;

  public SuggestConnectionParams(Map<String, List<ConnectionSuggestionDto>> suggestionsByConfigScopeId) {
    this.suggestionsByConfigScopeId = suggestionsByConfigScopeId;
  }

  public Map<String, List<ConnectionSuggestionDto>> getSuggestionsByConfigScopeId() {
    return suggestionsByConfigScopeId;
  }

}
