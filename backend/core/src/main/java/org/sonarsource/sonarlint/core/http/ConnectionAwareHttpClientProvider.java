/*
ACR-ae0b79420938423ca8f65b0b86e0bd21
ACR-367246c7a63e4f18875c1f680fa3d75e
ACR-272f4b1ed2ec40a597b2d4b5c162269d
ACR-c8e81eafb77342e4bd23ae9aae068f22
ACR-030598aa6f3a49e8b6d0bdaa5ebc7e68
ACR-c1141ce4b9da43e89e7d67eb1833b2e5
ACR-f5fb9f31d8984dd68c1e4a469cfebc86
ACR-c5feb3a62a134c5594baa36b97e5fbb7
ACR-631a669d03d0419da9d61735f8aceae1
ACR-4b8a6c3eaa0f4f7f99a528e0728aa724
ACR-9bd217ac2060463497212133e1d93e04
ACR-8a7617e83a414012b608a5ea9ed11d51
ACR-09392a7ef882439da115f98a46d9fa48
ACR-549ee320ad8d454c837ce19b4e50f897
ACR-ad7e4fe2e72a4a34b5dbe3129f56f59b
ACR-aaee71f6980e4a9f87bf915c03864323
ACR-d728c97bb4174a4da31a240fe178b093
 */
package org.sonarsource.sonarlint.core.http;

import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetCredentialsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sync.InvalidTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class ConnectionAwareHttpClientProvider {
  private final SonarLintRpcClient client;
  private final HttpClientProvider httpClientProvider;

  public ConnectionAwareHttpClientProvider(SonarLintRpcClient client, HttpClientProvider httpClientProvider) {
    this.client = client;
    this.httpClientProvider = httpClientProvider;
  }

  public HttpClient getHttpClient() {
    return httpClientProvider.getHttpClient();
  }

  public HttpClient getHttpClient(String connectionId, boolean shouldUseBearer) {
    try {
      var credentials = queryClientForConnectionCredentials(connectionId);
      return credentials.map(
        tokenDto -> httpClientProvider.getHttpClientWithPreemptiveAuth(tokenDto.getToken(), shouldUseBearer),
        userPass -> httpClientProvider.getHttpClientWithPreemptiveAuth(userPass.getUsername(), userPass.getPassword()));
    } catch (Exception e) {
      client.invalidToken(new InvalidTokenParams(connectionId));
      throw e;
    }
  }

  public WebSocketClient getWebSocketClient(String connectionId) {
    var credentials = queryClientForConnectionCredentials(connectionId);
    if (credentials.isRight()) {
      //ACR-c1d6b957738e4a6a8f9ec9e7e5216ec5
      throw new IllegalStateException("Expected token for connection " + connectionId);
    }
    return httpClientProvider.getWebSocketClient(credentials.getLeft().getToken());
  }

  private Either<TokenDto, UsernamePasswordDto> queryClientForConnectionCredentials(String connectionId) {
    var response = client.getCredentials(new GetCredentialsParams(connectionId)).join();
    var credentials = response.getCredentials();
    validateCredentials(connectionId, credentials);
    return credentials;
  }

  private static void validateCredentials(String connectionId, @Nullable Either<TokenDto, UsernamePasswordDto> credentials) {
    if (credentials == null) {
      throw new IllegalStateException("No credentials for connection " + connectionId);
    }
    if (credentials.isLeft()) {
      if(isNullOrEmpty(credentials.getLeft().getToken())) {
        throw new IllegalStateException("No token for connection " + connectionId);
      }
      return;
    }
    var right = credentials.getRight();
    if (right == null) {
      throw new IllegalStateException("No username/password for connection " + connectionId);
    }
    if (isNullOrEmpty(right.getUsername())) {
      throw new IllegalStateException("No username for connection " + connectionId);
    }
    if (isNullOrEmpty(right.getPassword())) {
      throw new IllegalStateException("No password for connection " + connectionId);
    }
  }

  private static boolean isNullOrEmpty(@Nullable String s) {
    return s == null || s.trim().isEmpty();
  }
}
