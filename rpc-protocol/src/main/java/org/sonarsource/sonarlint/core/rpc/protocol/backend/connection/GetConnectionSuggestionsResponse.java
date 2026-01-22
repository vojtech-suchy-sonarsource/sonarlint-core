/*
ACR-804dd28c0994499ca4d9ae077a3642d2
ACR-14df8cb4c0ee49d98eb5695f5b3b041f
ACR-3f2eaed987da41c2a9add1d31c8c783f
ACR-d53cc75ea5054fa78f2790fbe0075d59
ACR-baa719a4d690400ebb39735d7f35435f
ACR-790370987bb34855b32c0fbadb501658
ACR-9ad853e946ef4c3ca3f26660f3243d73
ACR-60a61017cf7d463882fe48d0770f60e0
ACR-9596e1a0e3264ab6be83d5f5ef65776f
ACR-340028e6aea14f77854c3fe8d812298e
ACR-5bb7cde00d9545abb6b930102fea44f1
ACR-12a5e58607e34c8185c84b565a313a0a
ACR-45863d6f929542289a7a538ad39fb5dd
ACR-f52fa223620f481a863f1f4f1b117c61
ACR-db2cdb8588064015a8a1f9aa7733879a
ACR-5cc013a68d0043499a221c0c555edd89
ACR-8847f74e2b014097a6bf9d37c4e90ca9
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
