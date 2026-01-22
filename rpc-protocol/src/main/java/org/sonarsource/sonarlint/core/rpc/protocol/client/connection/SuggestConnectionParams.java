/*
ACR-ece86ec6aa8c44b0a15caa947bfca31e
ACR-b804490e0e1a4c769a715e0670ad00d3
ACR-e5ad3c6db68e47e9a538577876871d05
ACR-2e715465e8274374a6b12d26c93dbcf3
ACR-94adcbf9251b4485b06b0490639313c9
ACR-9b9d4da15d454d31800783182dba6467
ACR-2bdab346cc4d4668b679bf408b5b09c3
ACR-2e7c7a3e93734b858bf846d0a14b97b6
ACR-c2de57b6ee5e469f9dc57d5dc04f933e
ACR-ae17d33c6eda4a399d1e9f807964fbf1
ACR-e05c841a9d4e4a8cac8d7457362aaa67
ACR-2fd6151164e14d0a922823390675c46b
ACR-5101df5b51524c1dbf55f2a29d1e7e06
ACR-7dd8b797259641dbad84926e0ef5eca7
ACR-33b71990ee3647cca6546bc36e6407cd
ACR-e12cfe38d40e487a963d6caf939c4ada
ACR-c25430714d5543cc9bd7657c442359ec
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
