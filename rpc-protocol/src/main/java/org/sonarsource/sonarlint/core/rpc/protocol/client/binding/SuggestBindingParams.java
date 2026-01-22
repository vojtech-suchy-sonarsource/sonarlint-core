/*
ACR-81b6cf295d09456dab8d2cb06e911440
ACR-824911977fa240c7904cc792a3e9f187
ACR-dc7cd2c5a85847e292cbddee7551a10d
ACR-cf9d36d9068f4af6bec7b94f4ca70527
ACR-9a1bc32875e34b12b44070965aa8d50e
ACR-49c7768d07e9493d9011dc7a056ccaa2
ACR-dad4d28afbae44628bfe45bff9e26842
ACR-28107bb60ecc4ce990d854090abd3811
ACR-017bd6d914794e9d94b576d5ed8e5c26
ACR-5a730332a1194433bff8318431e70cc6
ACR-5c16c9cb217d41e19c01ddb46c7ee931
ACR-76ff9b2823e64efa966873f4a6a13ae5
ACR-b57a827ef9e64c1f856e87e77650e70f
ACR-7769c25c54994db1a8cf3308b720d49d
ACR-2628197f71d64ec7a5fea66abc0ad88e
ACR-3ef65aa60bb04a0fb9887142e14e543e
ACR-795c776179ef409c84fd992935f8ad69
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
