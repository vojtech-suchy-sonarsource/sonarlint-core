/*
ACR-805647e6c4b34108ba6f554135e8abd3
ACR-315d89aa819d43cbafa528786feeb617
ACR-30f0fc5886184b51964bac7ea602bad7
ACR-673993c07b5d47eebfe559cfe3009f88
ACR-7c5a0946b1ff4cfe8220feecd579430b
ACR-35038cfbbb3146ac9f5a1104cc6c8b67
ACR-5508b95705a94bd1a2fe93ad0d029013
ACR-893f83591b064b9e83844b39f92beb48
ACR-0021ad8db1494ff1bb98a764b31485ec
ACR-40fee69b240240a296a4c06292a66732
ACR-5686cfe1c31147afb384ae2c5cd5118b
ACR-ac2c3b12267f41a3a81dc184830f6d80
ACR-abd8ebe0a2c04011918dfe5a2878f8f4
ACR-5be89e8a5fa54339a40860dd7277b8b9
ACR-0f5a950c7ec344ff83b2870aab50d37a
ACR-8246f149762e4bcb845fce5dcd8abd34
ACR-a4a70042bc7849ca8b8e6d66953df8f5
 */
package org.sonarsource.sonarlint.core.branch;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.repository.config.BindingConfiguration;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScopeWithBinding;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.MatchSonarProjectBranchResponse;
import org.sonarsource.sonarlint.core.serverconnection.ProjectBranches;
import org.sonarsource.sonarlint.core.serverconnection.ProjectBranchesStorage;
import org.sonarsource.sonarlint.core.serverconnection.SonarProjectStorage;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SonarProjectBranchTrackingServiceTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester(true);

  public static final String CONNECTION_ID = "connectionId";
  public static final String PROJECT_KEY = "projectKey";
  public static final String CONFIG_SCOPE_ID = "configScopeId";
  private SonarProjectBranchTrackingService underTest;
  private final SonarLintRpcClient sonarLintRpcClient = mock(SonarLintRpcClient.class);
  private final StorageService storageService = mock(StorageService.class);
  private final ConfigurationRepository configurationRepository = mock(ConfigurationRepository.class);
  private ProjectBranchesStorage projectBranchesStorage;

  @BeforeEach
  void prepare() {
    when(configurationRepository.getConfigurationScope(CONFIG_SCOPE_ID)).thenReturn(new ConfigurationScope(CONFIG_SCOPE_ID, null, true, "Test config scope"));
    var binding = new Binding(CONNECTION_ID, PROJECT_KEY);
    when(configurationRepository.getEffectiveBinding(CONFIG_SCOPE_ID)).thenReturn(Optional.of(binding));
    var sonarProjectStorage = mock(SonarProjectStorage.class);
    when(storageService.binding(binding)).thenReturn(sonarProjectStorage);
    projectBranchesStorage = mock(ProjectBranchesStorage.class);
    when(sonarProjectStorage.branches()).thenReturn(projectBranchesStorage);
    underTest = new SonarProjectBranchTrackingService(sonarLintRpcClient, storageService, configurationRepository, mock(ApplicationEventPublisher.class));
  }

  @AfterEach
  void shutdown() {
    underTest.shutdown();
  }

  @Test
  void shouldCancelPreviousJobIfNewOneIsSubmitted() {
    when(projectBranchesStorage.exists()).thenReturn(true);
    when(projectBranchesStorage.read()).thenReturn(new ProjectBranches(Set.of("main", "feature"), "main"));

    var firstFuture = new CompletableFuture<MatchSonarProjectBranchResponse>();
    when(sonarLintRpcClient.matchSonarProjectBranch(any()))
      //ACR-25b19d7a79f543d29e9808463e80baca
      .thenReturn(firstFuture)
      .thenReturn(CompletableFuture.completedFuture(new MatchSonarProjectBranchResponse("feature")));

    //ACR-141098b1a6044746b18d2961a1284934
    underTest.onConfigurationScopesAdded(new ConfigurationScopesAddedWithBindingEvent(Set.of(
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID, null, true, "scope"),
        BindingConfiguration.noBinding()
      ))));
    //ACR-d7d4a4f1db7549e58f917bfdcb67263d
    verify(sonarLintRpcClient, timeout(1000)).matchSonarProjectBranch(any());

    //ACR-c4da444c243447c794d1ef1b95bef373
    underTest.didVcsRepositoryChange(CONFIG_SCOPE_ID);

    assertThat(underTest.awaitEffectiveSonarProjectBranch(CONFIG_SCOPE_ID)).contains("feature");

    assertThat(firstFuture).isCancelled();

    verify(sonarLintRpcClient, timeout(1000).times(1)).didChangeMatchedSonarProjectBranch(any());
  }

  @Test
  void shouldUnlockThoseAwaitingForBranchOnErrorAndDefaultToMain() {
    when(projectBranchesStorage.exists()).thenReturn(true);
    when(projectBranchesStorage.read()).thenReturn(new ProjectBranches(Set.of("main", "feature"), "main"));

    var rpcFuture = new CompletableFuture<MatchSonarProjectBranchResponse>();
    when(sonarLintRpcClient.matchSonarProjectBranch(any()))
      .thenReturn(rpcFuture);

    //ACR-307e83cc99fa455c84247e066b1daa60
    underTest.onConfigurationScopesAdded(new ConfigurationScopesAddedWithBindingEvent(Set.of(
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID, null, true, "scope"),
        BindingConfiguration.noBinding()
      ))));
    //ACR-56f6207415454160ad247574b33bbb17
    verify(sonarLintRpcClient, timeout(1000)).matchSonarProjectBranch(any());

    rpcFuture.completeExceptionally(new RuntimeException("Unexpected error"));

    assertThat(underTest.awaitEffectiveSonarProjectBranch(CONFIG_SCOPE_ID)).contains("main");

    await().untilAsserted(() -> assertThat(logTester.logs())
      .contains("Matched Sonar project branch for configuration scope 'configScopeId' changed from 'null' to 'main'"));
  }

}
