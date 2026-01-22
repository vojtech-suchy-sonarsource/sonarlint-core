/*
ACR-eff49a3da8344a1da219fde4947acfb2
ACR-02839fef93ff4d0d83cb423ee9fee3e2
ACR-469b662a7a8b4bcb84ca0143b9940cf2
ACR-fd06873ccffa476899cf9410f5230ac5
ACR-a168e063576648f1877621510f5e091a
ACR-7c61537313a346938dee06e23af5e540
ACR-450d16775a0344679bad3b5770f1ec9a
ACR-a1422388099b4d0388732a6c81f1ff01
ACR-7b2a6f8717a144809e62189ece61a9f9
ACR-46386834780746b5998a6372ce8ed79c
ACR-b8f3cd122aae4d0eb67d2a00633b4f5c
ACR-5b45fb690c79407eae93dbaccff3aa0a
ACR-7525147784e843adb98905d2ce387ac9
ACR-a7145d3d3bd84097b13e974f05f56747
ACR-946f03c2dbcc47a8b9177e7185f87ad9
ACR-03a2369ff4424406a6437976429b5ac4
ACR-6b9ab22080ca468db31bd80db82af05b
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
