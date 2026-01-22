/*
ACR-97ce0836d8664b489e1feaf46dc4fd80
ACR-7f44abc0d1cd4ecfb63c856e5e325862
ACR-ef9ed31d31344057843c41832f7ae5df
ACR-aab33eeb05de4bc9b65d6aed9b1a1a70
ACR-ed15755a369e4258990c1669ba9f9203
ACR-885ae77c0be442a2bb79903c8ae01491
ACR-be2c24debe53487a9381b5b189bd8f81
ACR-2ea9e3c1d18f46948cde53ae872d81fb
ACR-967c8b4adaac42c4a096a91cf05623c6
ACR-ad78c69a90af44329c0bd28d408295df
ACR-4969a406611e4bf8bb4bd8a6d86abbb5
ACR-a30455846a824a108b784ce8a3f3263b
ACR-235b0c7ef145433492db4097715aee78
ACR-a3720a485115468eaca64cc3a9deb6cc
ACR-f18cf93779164e83b1fa1ce44b42ca14
ACR-c4768fb2265349d78b4456b57d34be3a
ACR-51430227c22742adbcee51ea1918ea08
 */
package org.sonarsource.sonarlint.core;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;
import org.sonarsource.sonarlint.core.serverapi.component.ServerProject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BindingCandidatesFinderTests {

  @RegisterExtension
  static final SonarLintLogTester logTester = new SonarLintLogTester();

  private ConfigurationRepository configRepository;
  private BindingClueProvider bindingClueProvider;
  private SonarProjectsCache sonarProjectsCache;
  private SonarLintCancelMonitor cancelMonitor;

  private BindingCandidatesFinder underTest;

  @BeforeEach
  void setUp() {
    configRepository = mock(ConfigurationRepository.class);
    bindingClueProvider = mock(BindingClueProvider.class);
    sonarProjectsCache = mock(SonarProjectsCache.class);
    cancelMonitor = mock(SonarLintCancelMonitor.class);
    underTest = new BindingCandidatesFinder(configRepository, bindingClueProvider, sonarProjectsCache);
  }

  @Test
  void should_mark_scope_as_shared_configuration_when_any_clue_has_shared_config_origin() {
    var scope = new ConfigurationScope("scope1", null, true, "Test Scope");
    var sharedConfigClue = new BindingClueProvider.UnknownBindingClue("projectKey", BindingSuggestionOrigin.SHARED_CONFIGURATION);
    var propertiesFileClue = new BindingClueProvider.UnknownBindingClue("projectKey", BindingSuggestionOrigin.PROPERTIES_FILE);
    
    when(configRepository.getAllBindableUnboundScopes()).thenReturn(List.of(scope));
    when(bindingClueProvider.collectBindingCluesWithConnections("scope1", Set.of("conn1"), cancelMonitor))
      .thenReturn(List.of(
        new BindingClueProvider.BindingClueWithConnections(sharedConfigClue, Set.of("conn1")),
        new BindingClueProvider.BindingClueWithConnections(propertiesFileClue, Set.of("conn1"))
      ));

    var candidates = underTest.findConfigScopesToBind("conn1", "projectKey", cancelMonitor);

    assertThat(candidates).hasSize(1);
    var candidate = candidates.iterator().next();
    assertThat(candidate.getConfigurationScope()).isEqualTo(scope);
    assertThat(candidate.getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @Test
  void should_not_mark_scope_as_shared_configuration_when_no_clue_has_shared_config_origin() {
    var scope = new ConfigurationScope("scope1", null, true, "Test Scope");
    var propertiesFileClue = new BindingClueProvider.UnknownBindingClue("projectKey", BindingSuggestionOrigin.PROPERTIES_FILE);
    var remoteUrlClue = new BindingClueProvider.UnknownBindingClue("projectKey", BindingSuggestionOrigin.REMOTE_URL);
    
    when(configRepository.getAllBindableUnboundScopes()).thenReturn(List.of(scope));
    when(bindingClueProvider.collectBindingCluesWithConnections("scope1", Set.of("conn1"), cancelMonitor))
      .thenReturn(List.of(
        new BindingClueProvider.BindingClueWithConnections(propertiesFileClue, Set.of("conn1")),
        new BindingClueProvider.BindingClueWithConnections(remoteUrlClue, Set.of("conn1"))
      ));

    var candidates = underTest.findConfigScopesToBind("conn1", "projectKey", cancelMonitor);

    assertThat(candidates).hasSize(1);
    var candidate = candidates.iterator().next();
    assertThat(candidate.getConfigurationScope()).isEqualTo(scope);
    assertThat(candidate.getOrigin()).isEqualTo(BindingSuggestionOrigin.PROPERTIES_FILE);
  }

  @Test
  void should_select_project_name_when_name_matches_and_no_shared_or_properties_file_clues() {
    var scope = new ConfigurationScope("scope1", null, true, "MyProj");

    when(configRepository.getAllBindableUnboundScopes()).thenReturn(List.of(scope));
    when(bindingClueProvider.collectBindingCluesWithConnections("scope1", Set.of("conn1"), cancelMonitor))
      .thenReturn(List.of(
      ));
    when(sonarProjectsCache.getSonarProject("conn1", "projectKey", cancelMonitor))
      .thenReturn(Optional.of(new ServerProject("projectKey", "MyProj", false)));

    var candidates = underTest.findConfigScopesToBind("conn1", "projectKey", cancelMonitor);

    assertThat(candidates).hasSize(1);
    var candidate = candidates.iterator().next();
    assertThat(candidate.getConfigurationScope()).isEqualTo(scope);
    assertThat(candidate.getOrigin()).isEqualTo(BindingSuggestionOrigin.PROJECT_NAME);
  }
}
