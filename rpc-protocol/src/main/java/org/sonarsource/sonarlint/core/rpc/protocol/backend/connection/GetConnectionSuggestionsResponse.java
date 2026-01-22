/*
ACR-58e91c76ad484cbea424b03984963134
ACR-4f6ecc0b53e54cb6b38278d217686209
ACR-aeb77483169f418eb046c0ff1342293a
ACR-ad113125a7534f9ab26778b16b1e21be
ACR-39685a633ee74730b13a003446c8c9a9
ACR-c4f3c610efaa468597b74548a145be6a
ACR-01f028a9090f4c909f0344603ba5f9e9
ACR-921fdf28c00042e0882b3589d43d5c25
ACR-c7155019dce140949bb21bceabcee6ba
ACR-18f7423952e8436ca4e004edbe048905
ACR-020248cf10e14bdebfe4a2a1ab600f9a
ACR-0c8dcfb317c4499bb231220044c21fc5
ACR-cf1108830c0a4a4d928941f2b8b4a420
ACR-d0ca4bc655424e6dbdf011e7863ac7b1
ACR-ec5e818d059a48e390882dc418bdb7c8
ACR-f6d61dca05ff4037938d9731b0cc2deb
ACR-0adb25d893b347c29909d0d880ccfcd8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection;

import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.ConnectionSuggestionDto;

public class GetConnectionSuggestionsResponse {
  private final List<ConnectionSuggestionDto> connectionSuggestions;

  public GetConnectionSuggestionsResponse(List<ConnectionSuggestionDto> connectionSuggestions) {
    this.connectionSuggestions = connectionSuggestions;
  }

  public List<ConnectionSuggestionDto> getConnectionSuggestions() {
    return connectionSuggestions;
  }
}
