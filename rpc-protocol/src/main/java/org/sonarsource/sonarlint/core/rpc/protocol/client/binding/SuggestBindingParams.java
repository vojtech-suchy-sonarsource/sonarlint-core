/*
ACR-cb16c32ef35f48fca6b2eabde5a2ddb3
ACR-8871eab931f24b0c9b878ec0f12c33f6
ACR-77a2747f2d584b16b6db7f3d14f9e247
ACR-e44ac203c9444f11834dc5f16a41022a
ACR-09c81bbc92f149aa86deccc9aa1f7432
ACR-347613d1a1a14c288ba036a54305f098
ACR-5a972de467c842e6b3b8172acbd8d2b0
ACR-8c00ce44809a41208e6607501ba6223e
ACR-d24940ec25ff4263a69263aae91dfb74
ACR-cff2a01cd17a493eaff683ad2ac2736e
ACR-e3209d70067748fc818156cb7f409bfc
ACR-e016183119e749d0bc4a34ff11f883ab
ACR-857b2c372bbe47cb9e719b5bc63753fa
ACR-e9c5abcf35354be3a11da61ff1d0a4e7
ACR-2b97b1ddbdac49d99ef768a7808da8f3
ACR-abcac442d5b142c588644bc39dac26ce
ACR-b906b98c0afb48c58db94afce17b1512
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.binding;

import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionDto;

public class SuggestBindingParams {

  private final Map<String, List<BindingSuggestionDto>> suggestions;

  public SuggestBindingParams(Map<String, List<BindingSuggestionDto>> suggestions) {
    this.suggestions = suggestions;
  }

  public Map<String, List<BindingSuggestionDto>> getSuggestions() {
    return suggestions;
  }
}
