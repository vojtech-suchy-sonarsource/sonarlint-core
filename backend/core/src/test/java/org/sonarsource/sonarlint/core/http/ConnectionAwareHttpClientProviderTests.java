/*
ACR-7373682c86d24de5a8ae3402d997dc86
ACR-ad0acf3670d94f909ce6fc785cca8abc
ACR-39e2a8d6db5c4cd4a06ac4baadba5f8c
ACR-caaae8d2f5fd4555b724d4580c85fbb8
ACR-3cf12489e5d64ccda13e1955d110d770
ACR-7307543e3ab04b8fb7e806f8426fcce4
ACR-89e557860ca046b7af451c2712afedcb
ACR-0f1393873fc348ddac039aa0ff7df4bf
ACR-18bc91669782498eb1d277e963108da9
ACR-9d38a209c356442b961b9f7a0cc520b1
ACR-566b55b8abfd4aebbcf10402227596f0
ACR-96ae21be832f4e808509df65e15bd602
ACR-0d7b0f0968a244c6bfaa0bf6f41b2e16
ACR-7360e4eecd0a4efb94fb27a78b377142
ACR-213376dfcf4c4a6fb80a589678a61a2b
ACR-c71b19e1c7ec4e29bfee5ce66a48c6e0
ACR-1cb25235e2b740a8bfb3ac9f8b722ed2
 */
package org.sonarsource.sonarlint.core.http;

import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetCredentialsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetCredentialsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sync.InvalidTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConnectionAwareHttpClientProviderTests {

  private SonarLintRpcClient client;
  private ConnectionAwareHttpClientProvider underTest;

  @BeforeEach
  void setUp() {
    client = mock(SonarLintRpcClient.class);
    HttpClientProvider httpClientProvider = mock(HttpClientProvider.class);
    underTest = new ConnectionAwareHttpClientProvider(client, httpClientProvider);
  }

  @Test
  void should_call_invalidToken_notification_when_token_is_null() {
    String connectionId = "test-connection";
    boolean shouldUseBearer = true;

    var nullToken = Either.<TokenDto, UsernamePasswordDto> forLeft(new TokenDto(null));
    var response = new GetCredentialsResponse(nullToken);
    when(client.getCredentials(any(GetCredentialsParams.class)))
      .thenReturn(CompletableFuture.completedFuture(response));

    assertThatThrownBy(() -> underTest.getHttpClient(connectionId, shouldUseBearer))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("No token for connection " + connectionId);
    var paramsCaptor = ArgumentCaptor.forClass(InvalidTokenParams.class);
    verify(client).invalidToken(paramsCaptor.capture());
    var capturedParams = paramsCaptor.getValue();
    assertThat(capturedParams.getConnectionId()).isEqualTo(connectionId);
  }
} 
