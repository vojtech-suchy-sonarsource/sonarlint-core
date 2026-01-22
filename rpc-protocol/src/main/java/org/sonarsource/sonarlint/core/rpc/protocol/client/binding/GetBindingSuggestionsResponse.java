/*
ACR-dd214046b7964014b238f03dd6746482
ACR-cd5fde7abc2a4938b98aca57128b53e0
ACR-75cf17c278c24ef193f30075351fdaa9
ACR-6a81137e71784410bfd307571fa6efe5
ACR-5d168e73ec4e46a095bcaf644a3c8884
ACR-b8718179fb1347b9a9efd65fd74b777a
ACR-a39116c9228a400890296209e54dbe63
ACR-5545af1166334e8eb54e6fb5d1211dda
ACR-d03cbc2775824563bb0e002eaaecb174
ACR-29a39deafd924c108ab32ccf1723dc71
ACR-04b416457eb64ca797b8f003817daf1e
ACR-1de8e1ca52714289a205ef3c26e0293b
ACR-97104ce37aa149698de42df9ee98733f
ACR-5ebb92a9fb64418fa2f977c0f641dd17
ACR-cb78d0ea3f9e45d6b59629fe5e4d02ce
ACR-a3a1c824adb4458bae5aa8eca83b9e8f
ACR-a4059567ff5f461f8aa14ea484aec134
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.binding;

import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionDto;

public class GetBindingSuggestionsResponse {

  private final Map<String, List<BindingSuggestionDto>> suggestions;

  public GetBindingSuggestionsResponse(Map<String, List<BindingSuggestionDto>> suggestions) {
    this.suggestions = suggestions;
  }

  public Map<String, List<BindingSuggestionDto>> getSuggestions() {
    return suggestions;
  }
}
