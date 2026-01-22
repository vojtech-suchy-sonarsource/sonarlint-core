/*
ACR-30ac2e96a8ef42f6a115799bc725f0d4
ACR-0007dd3946fb47c6b790d3174e6378ee
ACR-f864134db7cc48d3b6e8ccb87131cc0e
ACR-0da4b3d20a9344a5966c986a14f65c36
ACR-f1b0056fb56a4a198d97a7c114aef9bd
ACR-d128189f18ad457282f048f3b2c7e765
ACR-54f95688402144889e6a6017f47d4853
ACR-646dfcde5852493d84d595e1826f5f2d
ACR-53034ec51d60434988353d5ce0c8a7cf
ACR-4c03c4bbcfe943cdb78cf55b8d440293
ACR-e26389b1d967437b94990414711104f7
ACR-64ac7581a0b44b33b0fca37991e1fbd8
ACR-ea580c9736a3443a896bf43b72cba646
ACR-baacc40df00348e9b191de59c66e2beb
ACR-a60f47da75044eb1bf07574f34a6587c
ACR-0b49c60a515e469f96039c4ab98ca610
ACR-e5db5c65cd604b1b88ae681351b246d2
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
