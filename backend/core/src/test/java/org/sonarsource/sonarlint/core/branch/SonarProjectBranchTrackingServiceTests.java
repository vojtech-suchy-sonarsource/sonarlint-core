/*
ACR-72037888c41649ceb6ea65b815833576
ACR-609fc616d7b64ffb9465be2982e03f87
ACR-8756eb8e65244009bc44634ca1e0a44d
ACR-57c1da0dcdcd40bdb550b400fda036f5
ACR-bc9e74f749ec448bb0d22cc2d406a5ce
ACR-ccf10a68dd394debad2df765f5be1987
ACR-86b6f23c61d94fd3913e7935e21429bd
ACR-6521bed1f9f04b449155a70a862f26af
ACR-30deeb338d09464984e7b071e13ce9bf
ACR-2b7b1b132e894241887175da94626bf0
ACR-ab0060a836f443079b50262f9ba8105c
ACR-557ba91e6a164a869a7ef94243255c01
ACR-d00780cdba0740bda2752ebe863e2a5b
ACR-2b7dee110b51483a88d5ba8840b41485
ACR-44e4f363966045a98bac1a700a4d518a
ACR-d8fc6fccdf4d49a5b526de753e39a3ff
ACR-e9c3f826baa746d4abdcfd45dfab6d68
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
      //ACR-247daec9e8714b9fb5d159b32f72ac5e
      .thenReturn(firstFuture)
      .thenReturn(CompletableFuture.completedFuture(new MatchSonarProjectBranchResponse("feature")));

    //ACR-bf0ba3d2dfc54c84b8aa5b16936d2ff1
    underTest.onConfigurationScopesAdded(new ConfigurationScopesAddedWithBindingEvent(Set.of(
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID, null, true, "scope"),
        BindingConfiguration.noBinding()
      ))));
    //ACR-e8db0c5c3f2243d5ba5110ac3c39fbbf
    verify(sonarLintRpcClient, timeout(1000)).matchSonarProjectBranch(any());

    //ACR-f4b9cc76f7284e99aa36b1cf74f57672
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

    //ACR-107e366b8ab242f9b6d2144490fe7b4c
    underTest.onConfigurationScopesAdded(new ConfigurationScopesAddedWithBindingEvent(Set.of(
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID, null, true, "scope"),
        BindingConfiguration.noBinding()
      ))));
    //ACR-c6aba7cd9f4f49c8b0c0a5509b4c60d0
    verify(sonarLintRpcClient, timeout(1000)).matchSonarProjectBranch(any());

    rpcFuture.completeExceptionally(new RuntimeException("Unexpected error"));

    assertThat(underTest.awaitEffectiveSonarProjectBranch(CONFIG_SCOPE_ID)).contains("main");

    await().untilAsserted(() -> assertThat(logTester.logs())
      .contains("Matched Sonar project branch for configuration scope 'configScopeId' changed from 'null' to 'main'"));
  }

}
