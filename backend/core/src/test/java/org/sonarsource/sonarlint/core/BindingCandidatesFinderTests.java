/*
ACR-56c3af04862c45239b998ec9b7367351
ACR-d9873786cef2497eb5716c895fdd4d43
ACR-71037dace7984f2b96d7937d24c613c1
ACR-4c30c6c5746a4f50afccbe1ebf5d2656
ACR-17a32720616146d7958add1c648e8718
ACR-e6166995ef564e80baa6503c249dca3f
ACR-c30f3fef762143c69c286acd773118fa
ACR-d5138c0067d64be6aee7acda6de295e7
ACR-db9e648ac84a46b6845622679e600a1f
ACR-0877d2f3c9384c788dcf2849a0ab717e
ACR-ae4e5a9649cc41cfbd336581eb7c50ef
ACR-f8a090a736fb42a3b4eb2b1a30f27a26
ACR-49d4c40ecec04319a035ec11e6fd77c5
ACR-be3b7c15573e48de84f7c183ebd40493
ACR-3957cadbd989421aa7e4fbf398b27adb
ACR-4ec4ec26b07a47fc9ef04cffd5839259
ACR-bc14f5d8a056432abccad6f79afcce1b
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
