/*
ACR-00172245d9c74f9b9a91471f883d8b15
ACR-da9931e88a964e23b054f7f29eb93e52
ACR-d543da07199a4ee68644be9bcb3b6154
ACR-b041699a46d04768b45d5ede11a63733
ACR-0ad97cf9fca44fe1b4873f83adaa02ab
ACR-30b2d453bab84b6984815001ef568924
ACR-1f4c671cd5e64c8f8af727077d0d3355
ACR-cd3a7cfc92c647898f961052f3147c82
ACR-9c3e8f301c504fb8826ae9678e26bfec
ACR-c36743c3be6f4e1cbf733a7cd4166a9f
ACR-7e96d615f585415c84759931103424e1
ACR-1a4a99c633044053bffa2e6bcc8b4133
ACR-acda266fc47d4157bfc7a6b8a25286de
ACR-14a3cba9cf004ea5ab2942bce0ad90a2
ACR-0cfe7d27a6684cf9a44ac9af83e447c9
ACR-e9345fb674b14197968aae8ef7404628
ACR-6c85a8a8ebaf48d5b057d4ceb9e3abc6
 */
package org.sonarsource.sonarlint.core.embedded.server;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenResponse;

import static org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository.haveSameOrigin;

public class AwaitingUserTokenFutureRepository {
  private final ConcurrentHashMap<String, CompletableFuture<HelpGenerateUserTokenResponse>> awaitingFuturesByServerUrl = new ConcurrentHashMap<>();

  public void addExpectedResponse(String serverBaseUrl, CompletableFuture<HelpGenerateUserTokenResponse> futureResponse) {
    var previousFuture = awaitingFuturesByServerUrl.put(serverBaseUrl, futureResponse);
    if (previousFuture != null) {
      previousFuture.cancel(false);
    }
    futureResponse.whenComplete((r, e) -> awaitingFuturesByServerUrl.remove(serverBaseUrl, futureResponse));
  }

  public Optional<CompletableFuture<HelpGenerateUserTokenResponse>> consumeFutureResponse(String serverOrigin) {
    for (var iterator = awaitingFuturesByServerUrl.entrySet().iterator(); iterator.hasNext();) {
      var entry = iterator.next();
      if (haveSameOrigin(entry.getKey(), serverOrigin)) {
        iterator.remove();
        return Optional.of(entry.getValue());
      }
    }
    return Optional.empty();
  }

}
