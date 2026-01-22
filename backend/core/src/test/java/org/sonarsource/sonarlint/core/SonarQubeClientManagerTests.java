/*
ACR-7d17e9456e2f4a1da8eddb117ba655c3
ACR-81ae5e33317146f39197ebe26bbfc0cb
ACR-0a808ddd925a4819971b4571b713ab94
ACR-7c6179566b1e4efab2c26c142d147382
ACR-56c2d0d8930d4ee4a1c9bc489f96f32d
ACR-ea0bca1e2ba74442ac40da63550c2e9e
ACR-73ae5f98156c4d2ea5e2d0f7c3f191d9
ACR-0d2b8e7b865b4bc3b8deac26798f5c02
ACR-07e3bda8dfc6441d96a0403f9c6035d5
ACR-20b49a67a8724af4b9c1126d23e6691b
ACR-96e662304b5f47999c24bf0cfca1cb54
ACR-ca7cda2545f046729b9860224106e3c4
ACR-f32f87d10fcb45d8aeff949da5f3d788
ACR-a6c5b53e0053499b99332447e1802223
ACR-37b74512521047f189f3f11db5e59ff6
ACR-50850c16cd354cba8738bfaaf1b5f38b
ACR-a80f6e3a0c394574a7fc5b39a60529d8
 */
package org.sonarsource.sonarlint.core;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.http.ConnectionAwareHttpClientProvider;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.SonarQubeConnectionConfiguration;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.serverapi.exception.UnauthorizedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SonarQubeClientManagerTests {
  private static final String API_SYSTEM_STATUS = "/api/system/status";

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private final ConnectionConfigurationRepository connectionRepository = mock(ConnectionConfigurationRepository.class);
  private final ConnectionAwareHttpClientProvider awareHttpClientProvider = mock(ConnectionAwareHttpClientProvider.class);
  private final HttpClientProvider httpClientProvider = mock(HttpClientProvider.class);
  private SonarLintRpcClient client;
  private SonarQubeClientManager underTest;

  @BeforeEach
  void setUp() {
    client = mock(SonarLintRpcClient.class);
    underTest = new SonarQubeClientManager(connectionRepository, awareHttpClientProvider, httpClientProvider,
      SonarCloudActiveEnvironment.prod(), client);
  }

  @Test
  void getClientOrThrow_for_sonarqube() {
    setupServerConnection("sqs1", "serverUrl");

    var connection = underTest.getClientOrThrow("sqs1");

    assertThat(connection.isActive()).isTrue();
  }

  @Test
  void getClientOrThrow_for_sonarcloud() {
    setupCloudConnection("sqc1", SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri());

    var connection = underTest.getClientOrThrow("sqc1");

    assertThat(connection.isActive()).isTrue();
  }

  @Test
  void getClientOrThrow_for_sonarcloud_with_trailing_slash_notConnected() {
    var uriWithSlash = URI.create(SonarCloudRegion.EU.getProductionUri() + "/");
    setupCloudConnection("sqc-with-slash", uriWithSlash, SonarCloudRegion.EU.getApiProductionUri());

    var connection = underTest.getClientOrThrow("sqc-with-slash");

    assertThat(connection.isActive()).isTrue();
  }

  @Test
  void getClientOrThrow_should_throw_if_connection_doesnt_exists() {
    var throwable = catchThrowable(() -> underTest.getClientOrThrow("sqc1"));

    assertThat(throwable.getMessage()).isEqualTo("Connection 'sqc1' is gone");
  }

  @Test
  void withActiveClient_should_execute_consumer_when_valid_client_exists() {
    setupServerConnection("sqs1", "serverUrl");
    var consumerExecuted = new AtomicBoolean(false);

    underTest.withActiveClient("sqs1", api -> consumerExecuted.set(true));

    assertThat(consumerExecuted.get()).isTrue();
  }

  @Test
  void withActiveClient_should_not_execute_consumer_when_connection_not_found() {
    when(connectionRepository.getConnectionById("nonexistent")).thenReturn(null);
    var consumerExecuted = new AtomicBoolean(false);

    underTest.withActiveClient("nonexistent", api -> consumerExecuted.set(true));

    assertThat(consumerExecuted.get()).isFalse();
    assertThat(logTester.logs()).contains("Connection 'nonexistent' is gone");
  }

  @Test
  void withActiveClient_should_not_execute_consumer_and_notify_user_when_client_becomes_inactive() {
    setupServerConnection("sqs1", "serverUrl");
    var consumerExecuted = new AtomicBoolean(false);

    underTest.withActiveClient("sqs1", api -> { throw new UnauthorizedException("401"); });
    underTest.withActiveClient("sqs1", api -> consumerExecuted.set(true));

    assertThat(consumerExecuted.get()).isFalse();
    assertThat(logTester.logs()).contains("Connection 'sqs1' is invalid");
    verify(client, times(1)).invalidToken(any());
  }

  @Test
  void withActiveClient_should_cache_clients_and_reuse_them() {
    setupServerConnection("sqs1", "serverUrl");
    var executionCount = new AtomicInteger(0);

    underTest.withActiveClient("sqs1", api -> executionCount.incrementAndGet());
    underTest.withActiveClient("sqs1", api -> executionCount.incrementAndGet());
    underTest.withActiveClient("sqs1", api -> executionCount.incrementAndGet());

    assertThat(executionCount.get()).isEqualTo(3);
    verify(awareHttpClientProvider, times(1)).getHttpClient("sqs1", false);
  }

  @Test
  void withActiveClientAndReturn_should_return_value_when_valid_client_exists() {
    setupServerConnection("sqs1", "serverUrl");

    var result = underTest.withActiveClientAndReturn("sqs1", api -> "test-result");

    assertThat(result).isPresent().get().isEqualTo("test-result");
  }

  @Test
  void withActiveClientAndReturn_should_return_empty_when_connection_not_found() {
    when(connectionRepository.getConnectionById("nonexistent")).thenReturn(null);
    var result = underTest.withActiveClientAndReturn("nonexistent", api -> "test-result");
    assertThat(result).isEmpty();
  }

  @Test
  void withActiveClientAndReturn_should_return_empty_when_client_inactive() {
    setupServerConnection("sqs1", "serverUrl");

    underTest.withActiveClient("sqs1", api -> { throw new UnauthorizedException("401"); });

    var result = underTest.withActiveClientAndReturn("sq1", api -> "test-result");
    assertThat(result).isEmpty();
  }

  @Test
  void withActiveClientFlatMapOptionalAndReturn_should_return_optional_when_valid_client_exists() {
    setupServerConnection("sqs1", "serverUrl");

    var result = underTest.withActiveClientFlatMapOptionalAndReturn("sqs1", api -> Optional.of("test-result"));

    assertThat(result).isPresent().get().isEqualTo("test-result");
  }

  @Test
  void withActiveClientFlatMapOptionalAndReturn_should_return_empty_when_function_returns_empty() {
    setupServerConnection("sqs1", "serverUrl");

    var result = underTest.withActiveClientFlatMapOptionalAndReturn("sqs1", api -> Optional.empty());

    assertThat(result).isEmpty();
  }

  @Test
  void withActiveClientFlatMapOptionalAndReturn_should_return_empty_when_connection_not_found() {
    var result = underTest.withActiveClientFlatMapOptionalAndReturn("nonexistent", api -> Optional.of("test-result"));

    assertThat(result).isEmpty();
  }

  @Test
  void withActiveClient_should_not_execute_consumer_when_invalid_credentials() {
    var httpClient = mock(HttpClient.class);
    when(awareHttpClientProvider.getHttpClient()).thenReturn(httpClient);
    setupSuccessfulStatusResponse(httpClient, "serverUrl" + API_SYSTEM_STATUS);
    when(connectionRepository.getConnectionById("connectionId"))
      .thenReturn(new SonarQubeConnectionConfiguration("connectionId", "serverUrl", true));
    when(awareHttpClientProvider.getHttpClient("connectionId", false))
      .thenThrow(new IllegalStateException("No token was provided"));

    var consumerExecuted = new AtomicBoolean(false);

    underTest.withActiveClient("connectionId", api -> consumerExecuted.set(true));

    assertThat(consumerExecuted.get()).isFalse();
  }

  private void setupServerConnection(String connectionId, String serverUrl) {
    when(connectionRepository.getConnectionById(connectionId))
      .thenReturn(new SonarQubeConnectionConfiguration(connectionId, serverUrl, true));
    var httpClient = mock(HttpClient.class);
    when(awareHttpClientProvider.getHttpClient(connectionId, false)).thenReturn(httpClient);
    when(awareHttpClientProvider.getHttpClient()).thenReturn(httpClient);
    setupSuccessfulStatusResponse(httpClient, serverUrl + API_SYSTEM_STATUS);
  }

  private void setupCloudConnection(String connectionId, URI prodUri, URI apiUri) {
    when(connectionRepository.getConnectionById(connectionId))
      .thenReturn(new SonarCloudConnectionConfiguration(prodUri, apiUri, connectionId, "organizationKey", SonarCloudRegion.EU, false));
    var httpClient = mock(HttpClient.class);
    when(awareHttpClientProvider.getHttpClient(connectionId, true)).thenReturn(httpClient);
    when(awareHttpClientProvider.getHttpClient()).thenReturn(httpClient);
    setupSuccessfulStatusResponse(httpClient, API_SYSTEM_STATUS);
  }

  private void setupSuccessfulStatusResponse(HttpClient httpClient, String statusPath) {
    var httpResponse = mock(HttpClient.Response.class);
    when(httpResponse.isSuccessful()).thenReturn(true);
    when(httpResponse.bodyAsString()).thenReturn("{\"id\": \"20160308094653\",\"version\": \"9.9\",\"status\": \"UP\"}");
    when(httpClient.getAsyncAnonymous(statusPath)).thenReturn(CompletableFuture.completedFuture(httpResponse));
  }
}
