/*
ACR-835fe4fc37de443998e7f20b3f0aad7a
ACR-3b767006a90348fb9e7cfa5308cb3b23
ACR-cd89f73064004c24a3f0b010eef01002
ACR-690fb8215e7644109c15bc6cca6dac94
ACR-0bb43bf085ba4bbdbe85f51f46f4fee8
ACR-332773abefae4a0aae78127683453920
ACR-7abf0319f74c490887ff1a6f093c952e
ACR-a27d2238c6114f61b44b565bd46d64f3
ACR-79b00f87b7434e16b22b6c7927d64197
ACR-e51dbe56951b4306bb212daadf5cf373
ACR-4e65290fd1844c8f99e69b8aad0abe5e
ACR-3d3e5c5b3829428ab0b4dfd9dd4cf97f
ACR-a81963a78cf94f3e8b6f7ea6aaddc6f2
ACR-4a6411a0d6b74175a483e256e0ffd381
ACR-e832ff169af9419891f3d874283fff7d
ACR-aed2c440f99f4ae4826ce98feec0a63e
ACR-034939ff2a8d4dc294545a93d33e2e51
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
