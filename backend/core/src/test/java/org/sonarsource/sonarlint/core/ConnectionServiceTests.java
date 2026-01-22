/*
ACR-f01243e063d341d3a1b104b632196599
ACR-27ea08e25ec34f0a89c6ee8f7d56c348
ACR-71d52b53f96b4e4cba668f012ea5ec12
ACR-59ba6d30b9a9406bbc2d775094e3f096
ACR-bf73c2bcf89b42988e1c140c4d3b4423
ACR-2b6e09c411b84b5ea4e67edd09f2663d
ACR-aad1a66eb74545fb97c298aee5a44e15
ACR-a8e3c6f1e90e424aad47ac49121e4222
ACR-b324fdd5ae9440ee87a86c77d68e5779
ACR-a93306597e6c40e797cb2ddbdf1ff1b9
ACR-3d9289825c7341758e28d87e6926429e
ACR-d37c4fdda9f3467fa870c48369a229ca
ACR-b72d4b922b0d488d84f74c2a29c75747
ACR-1ad768a83c934079a90c4eaedddb44bc
ACR-d16ab573626948d2a710b01434cba871
ACR-b0ace258903b4c72aa33218bbefb5abe
ACR-10cf93be9dd64ad09c1f3ef82946d037
 */
package org.sonarsource.sonarlint.core;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationAddedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationUpdatedEvent;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.SonarQubeConnectionConfiguration;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarCloudConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ConnectionServiceTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private final ConnectionConfigurationRepository repository = new ConnectionConfigurationRepository();

  public static final SonarQubeConnectionConfigurationDto SQ_DTO_1 = new SonarQubeConnectionConfigurationDto("sq1", "http://url1/", true);
  public static final SonarQubeConnectionConfigurationDto SQ_DTO_1_DUP = new SonarQubeConnectionConfigurationDto("sq1", "http://url1_dup/", true);
  public static final SonarQubeConnectionConfigurationDto SQ_DTO_2 = new SonarQubeConnectionConfigurationDto("sq2", "url2", true);
  public static final SonarCloudConnectionConfigurationDto SC_DTO_1 = new SonarCloudConnectionConfigurationDto("sc1", "org1", org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, true);
  public static final SonarCloudConnectionConfigurationDto SC_DTO_2 = new SonarCloudConnectionConfigurationDto("sc2", "org2", org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, true);
  private static final String EXPECTED_MESSAGE = "UTM parameters should match regular expression: [a-z0-9\\-]+";

  ApplicationEventPublisher eventPublisher;
  ConnectionService underTest;

  @BeforeEach
  void setUp() {
    eventPublisher = mock(ApplicationEventPublisher.class);
  }

  @Test
  void initialize_provide_connections() {
    underTest = new ConnectionService(eventPublisher, repository, List.of(SQ_DTO_1, SQ_DTO_2), List.of(SC_DTO_1, SC_DTO_2), SonarCloudActiveEnvironment.prod(), null, null);

    assertThat(repository.getConnectionsById()).containsOnlyKeys("sq1", "sq2", "sc1", "sc2");
  }

  @Test
  void generate_user_token_should_ignore_null_utm() {
    SonarLintCancelMonitor cancelMonitor = new SonarLintCancelMonitor();
    TokenGeneratorHelper mockedHelper = mock(TokenGeneratorHelper.class);
    when(mockedHelper.helpGenerateUserToken("serverUrl", null, cancelMonitor))
      .thenReturn(new HelpGenerateUserTokenResponse("TOKEN"));
    underTest = new ConnectionService(eventPublisher, repository, List.of(), List.of(), SonarCloudActiveEnvironment.prod(), null, mockedHelper);

    var response = underTest.helpGenerateUserToken("serverUrl", null, cancelMonitor);

    assertThat(response.getToken()).isEqualTo("TOKEN");
  }

  @Test
  void generate_user_token_should_throw_validation_error_for_all() {
    TokenGeneratorHelper mockedHelper = mock(TokenGeneratorHelper.class);
    underTest = new ConnectionService(eventPublisher, repository, List.of(), List.of(), SonarCloudActiveEnvironment.prod(), null, mockedHelper);
    HelpGenerateUserTokenParams.Utm invalidParams = new HelpGenerateUserTokenParams.Utm("medium wrong", "source/", "contENT", "t.e.r.m");
    SonarLintCancelMonitor cancelMonitor = new SonarLintCancelMonitor();

    ResponseErrorException exception = catchThrowableOfType(ResponseErrorException.class, () ->
      underTest.helpGenerateUserToken("serverUrl", invalidParams, cancelMonitor));
    ResponseError innerError = exception.getResponseError();

    assertThat(exception).hasMessage(EXPECTED_MESSAGE);
    assertThat(innerError).extracting("message").isEqualTo(EXPECTED_MESSAGE);
    assertThat(innerError).extracting("code").isEqualTo(ResponseErrorCode.InvalidParams.getValue());
    assertThat(innerError).extracting("data").asInstanceOf(InstanceOfAssertFactories.array(String[].class))
      .containsExactlyInAnyOrder("utm_medium", "utm_source", "utm_content", "utm_term");
    verifyNoInteractions(mockedHelper);
  }

  @Test
  void generate_user_token_should_throw_validation_error_for_two() {
    TokenGeneratorHelper mockedHelper = mock(TokenGeneratorHelper.class);
    underTest = new ConnectionService(eventPublisher, repository, List.of(), List.of(), SonarCloudActiveEnvironment.prod(), null, mockedHelper);
    HelpGenerateUserTokenParams.Utm invalidParams = new HelpGenerateUserTokenParams.Utm("medium wrong", "source", "cont-ent", "t.e.r.m");
    SonarLintCancelMonitor cancelMonitor = new SonarLintCancelMonitor();

    ResponseErrorException exception = catchThrowableOfType(ResponseErrorException.class, () ->
      underTest.helpGenerateUserToken("serverUrl", invalidParams, cancelMonitor));
    ResponseError innerError = exception.getResponseError();

    assertThat(exception).hasMessage(EXPECTED_MESSAGE);
    assertThat(innerError).extracting("message").isEqualTo(EXPECTED_MESSAGE);
    assertThat(innerError).extracting("code").isEqualTo(ResponseErrorCode.InvalidParams.getValue());
    assertThat(innerError).extracting("data").asInstanceOf(InstanceOfAssertFactories.array(String[].class))
      .containsExactlyInAnyOrder("utm_medium", "utm_term");
    verifyNoInteractions(mockedHelper);
  }

  @Test
  void add_new_connection_and_post_event() {
    underTest = new ConnectionService(eventPublisher, repository, List.of(), List.of(), SonarCloudActiveEnvironment.prod(), null, null);

    underTest.didUpdateConnections(List.of(SQ_DTO_1), List.of());
    assertThat(repository.getConnectionsById()).containsOnlyKeys("sq1");
    assertThat(repository.getConnectionById("sq1"))
      .asInstanceOf(InstanceOfAssertFactories.type(SonarQubeConnectionConfiguration.class))
      .extracting(SonarQubeConnectionConfiguration::getConnectionId, SonarQubeConnectionConfiguration::getUrl, SonarQubeConnectionConfiguration::isDisableNotifications,
        SonarQubeConnectionConfiguration::getKind)
      .containsOnly("sq1", "http://url1", true, ConnectionKind.SONARQUBE);

    underTest.didUpdateConnections(List.of(SQ_DTO_1, SQ_DTO_2), List.of());
    assertThat(repository.getConnectionsById()).containsOnlyKeys("sq1", "sq2");

    underTest.didUpdateConnections(List.of(SQ_DTO_1, SQ_DTO_2), List.of(SC_DTO_1));
    assertThat(repository.getConnectionsById()).containsOnlyKeys("sq1", "sq2", "sc1");
    assertThat(repository.getConnectionById("sc1"))
      .asInstanceOf(InstanceOfAssertFactories.type(SonarCloudConnectionConfiguration.class))
      .extracting(SonarCloudConnectionConfiguration::getConnectionId, SonarCloudConnectionConfiguration::getUrl, SonarCloudConnectionConfiguration::isDisableNotifications,
        SonarCloudConnectionConfiguration::getKind, SonarCloudConnectionConfiguration::getOrganization)
      .containsOnly("sc1", "https://sonarcloud.io", true, ConnectionKind.SONARCLOUD, "org1");

    var captor = ArgumentCaptor.forClass(ConnectionConfigurationAddedEvent.class);
    verify(eventPublisher, times(3)).publishEvent(captor.capture());
    var events = captor.getAllValues();

    assertThat(events).extracting(ConnectionConfigurationAddedEvent::addedConnectionId).containsExactly("sq1", "sq2", "sc1");
  }

  @Test
  void multiple_connections_with_same_id_should_log_and_ignore() {
    underTest = new ConnectionService(eventPublisher, repository, List.of(), List.of(), SonarCloudActiveEnvironment.prod(), null, null);
    underTest.didUpdateConnections(List.of(SQ_DTO_1), List.of());

    underTest.didUpdateConnections(List.of(SQ_DTO_1, SQ_DTO_1_DUP), List.of());

    assertThat(repository.getConnectionById("sq1"))
      .asInstanceOf(InstanceOfAssertFactories.type(SonarQubeConnectionConfiguration.class))
      .extracting(SonarQubeConnectionConfiguration::getConnectionId, SonarQubeConnectionConfiguration::getUrl, SonarQubeConnectionConfiguration::isDisableNotifications,
        SonarQubeConnectionConfiguration::getKind)
      .containsOnly("sq1", "http://url1_dup", true, ConnectionKind.SONARQUBE);

    assertThat(logTester.logs(LogOutput.Level.ERROR)).containsExactly("Duplicate connection registered: sq1");
  }

  @Test
  void remove_connection() {
    underTest = new ConnectionService(eventPublisher, repository, List.of(SQ_DTO_1), List.of(SC_DTO_1), SonarCloudActiveEnvironment.prod(), null, null);
    assertThat(repository.getConnectionsById()).containsKeys("sq1", "sc1");

    underTest.didUpdateConnections(List.of(SQ_DTO_1), List.of());
    assertThat(repository.getConnectionsById()).containsKeys("sq1");

    underTest.didUpdateConnections(List.of(), List.of());
    assertThat(repository.getConnectionsById()).isEmpty();

    var captor = ArgumentCaptor.forClass(ConnectionConfigurationRemovedEvent.class);
    verify(eventPublisher, times(2)).publishEvent(captor.capture());
    var events = captor.getAllValues();

    assertThat(events).extracting(ConnectionConfigurationRemovedEvent::getRemovedConnectionId).containsExactly("sc1", "sq1");
  }

  @Test
  void remove_connection_should_log_if_unknown_connection_and_ignore() {
    var mockedRepo = mock(ConnectionConfigurationRepository.class);
    underTest = new ConnectionService(eventPublisher, mockedRepo, List.of(), List.of(), SonarCloudActiveEnvironment.prod(), null, null);

    //ACR-01783c6bd4504a06b9856a50e2fbea20
    when(mockedRepo.getConnectionsById()).thenReturn(Map.of("id", new SonarQubeConnectionConfiguration("id", "http://foo", true)));
    when(mockedRepo.remove("id")).thenReturn(null);

    underTest.didUpdateConnections(List.of(), List.of());

    assertThat(logTester.logs(LogOutput.Level.DEBUG)).containsExactly("Attempt to remove connection 'id' that was not registered. Possibly a race condition?");
  }

  @Test
  void update_connection() {
    underTest = new ConnectionService(eventPublisher, repository, List.of(SQ_DTO_1), List.of(), SonarCloudActiveEnvironment.prod(), null, null);

    underTest.didUpdateConnections(List.of(SQ_DTO_1_DUP), List.of());

    assertThat(repository.getConnectionById("sq1"))
      .asInstanceOf(InstanceOfAssertFactories.type(SonarQubeConnectionConfiguration.class))
      .extracting(SonarQubeConnectionConfiguration::getConnectionId, SonarQubeConnectionConfiguration::getUrl, SonarQubeConnectionConfiguration::isDisableNotifications,
        SonarQubeConnectionConfiguration::getKind)
      .containsOnly("sq1", "http://url1_dup", true, ConnectionKind.SONARQUBE);

    var captor = ArgumentCaptor.forClass(ConnectionConfigurationUpdatedEvent.class);
    verify(eventPublisher, times(1)).publishEvent(captor.capture());
    var events = captor.getAllValues();

    assertThat(events).extracting(ConnectionConfigurationUpdatedEvent::updatedConnectionId).containsExactly("sq1");
  }

  @Test
  void update_connection_should_log_if_unknown_connection_and_add() {
    var mockedRepo = mock(ConnectionConfigurationRepository.class);
    underTest = new ConnectionService(eventPublisher, mockedRepo, List.of(), List.of(), SonarCloudActiveEnvironment.prod(), null, null);

    //ACR-a5af597e2356458483163cc4e9b85870
    when(mockedRepo.getConnectionsById()).thenReturn(Map.of(SQ_DTO_2.getConnectionId(), new SonarQubeConnectionConfiguration(SQ_DTO_2.getConnectionId(), "http://foo", true)));
    when(mockedRepo.addOrReplace(any())).thenReturn(null);

    underTest.didUpdateConnections(List.of(SQ_DTO_2), List.of());

    assertThat(logTester.logs(LogOutput.Level.DEBUG)).containsExactly("Attempt to update connection 'sq2' that was not registered. Possibly a race condition?");
  }

}
