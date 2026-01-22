/*
ACR-5041b8dd30b04cd7b6cf7b140013a1de
ACR-3b5a8d5280b84a6187da8ab0167a6ab8
ACR-3b1c3f74e37541cbb6d5376f4c442d28
ACR-c90be44a850d41c2b92a125fabc1ac26
ACR-c14142068c46425ea633d604f7efc715
ACR-08cee2a6c6e4411882d8b2e4f6bae667
ACR-02f1315c9f2342d88943569febead3ed
ACR-f57a4699022347eabde26ba787d71e68
ACR-4fb3b92532f74a8e85838f5ebc0808d4
ACR-1d4be2a5de554874bfcd62b4e64a118c
ACR-0cffbb120aab4db787c59f514d9f02f2
ACR-1161e80de3ca4a17bfb6cad7068df5af
ACR-9a5bb8b034d3496ba6448da4115669a6
ACR-901e3b23f65549ce8808af2227146319
ACR-7336ed726f0849ed89d8afb6a069d525
ACR-f64462e7ca754c48a1a7006bdeb2ed65
ACR-0a2c2e3e0cba4e8a82ea47082ababf99
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
      //ACR-aca745762cdd4a6d9bfe79ae0877b3b2
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
