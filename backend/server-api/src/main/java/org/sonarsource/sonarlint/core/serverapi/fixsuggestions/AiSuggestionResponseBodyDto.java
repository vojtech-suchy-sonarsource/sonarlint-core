/*
ACR-ba2bee21b2244313956060a5cdf390af
ACR-380252bc218741e58f86f741f24daffc
ACR-7adb994d6f5f40ef9d2a72a16768305e
ACR-cd98fb27a5504abb9d6d97f0f155557d
ACR-a2c1323f30bf49d19f01151c9b721525
ACR-bfcc41bb96004721b873e01257d189f0
ACR-1f888416ea68497886d8973188514fbe
ACR-fbb13b319e2949b6b73a2bd3e5fd9067
ACR-9f92abe56df54b119a3a34279d30931c
ACR-51b8386e66594b95a449841cbf3d814f
ACR-f425144df8b84a69be0b6746852578be
ACR-f37121bb1e264d47b2bcf3305e38b011
ACR-48921b5cd75e4e9e844a9b95b5be6e52
ACR-4c108a72fb1e4ff392c1a63b58d0885a
ACR-c96e5ad45bf0479fbedce18a95308155
ACR-5beeb0aba8494fcc8f96a7f947c67e22
ACR-116c59536dc84eec84ec7293cc2cbfde
 */
package org.sonarsource.sonarlint.core.serverapi.fixsuggestions;

import java.util.List;
import java.util.UUID;

public record AiSuggestionResponseBodyDto(UUID id, String explanation, List<ChangeDto> changes) {
  public record ChangeDto(int startLine, int endLine, String newCode) {
  }
}
