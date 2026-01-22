/*
ACR-1a1b77f126a449a09cb38dfa593d63bd
ACR-eeac9afa79924e2a8a61d3d446ea3794
ACR-f97b58fe98094ca195597d594d0d0262
ACR-afc95327c4ff4bbd9bcb582b093c9ae9
ACR-08cad1486ba245a69588fd1bb68a1a0c
ACR-e80a54f8d98546d38580a171c1c7adce
ACR-1ad4d8aab40447479b498e1ad04f740e
ACR-12de2c6e042b4b05a51d63c01de753c0
ACR-c12cd4f401b049b9a7f11ef462d478a8
ACR-5d1c514e520340a1885e8c09c613750e
ACR-5bd64719b92a47468d4ec45e19a94d3f
ACR-78c83b611f8243ab980324d1bd1c6add
ACR-718be1ed0a1a4d23a4a75b42589732e0
ACR-a00e2c50362443039eec83c5c933dbcf
ACR-af10579b206e42c7a7256fb2bec604e2
ACR-dc8ef3e1fddb4dbebb2ae9d2a59e5e7e
ACR-b5bb288ade6c44378655aa38a1065bb4
 */
package org.sonarsource.sonarlint.core.serverapi.fixsuggestions;

import java.util.List;
import java.util.UUID;

public record AiSuggestionResponseBodyDto(UUID id, String explanation, List<ChangeDto> changes) {
  public record ChangeDto(int startLine, int endLine, String newCode) {
  }
}
