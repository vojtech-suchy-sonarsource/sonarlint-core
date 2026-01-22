/*
ACR-4ab7733cb3cd477982f907aa899ed30c
ACR-88480f4185074ddaa5e1ca6f62d3e45a
ACR-3fe28a81b92443d1b36bf48f60cb0a52
ACR-68e9ac0d94764f39b5c24634abdc201c
ACR-4b14864528424267a8a5b9ed5a378a45
ACR-edabd734d3e44154bcb0e827fccfb45b
ACR-d14a80625b3b4c84ba27036ba98de834
ACR-7eeb03af722c4d998880e93d4d8aa81b
ACR-df033934114c4eafaf1553a228a972a3
ACR-2271e57b3d284f1ca85ee70c3ec3376e
ACR-f65d8ed1a5dd43b1a2c247d16ed2d13b
ACR-539239849cbf4d09b16fb54fb072f884
ACR-5816c319f7114b179c04e5a26fe91252
ACR-91b46d13dc9842828180df84660024fa
ACR-d6f1efa491614f4d9acf6ad66eb37053
ACR-16e092dbfd0f4d56abb6feda0688ac71
ACR-11cbc4248b644311b6ebb687a3d39367
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
