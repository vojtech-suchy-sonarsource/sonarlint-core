/*
ACR-27fdfc3e27cb4e24be65f709430fb1c2
ACR-d3a4ea60bfac4a62977bdb95c332b842
ACR-c49a43bdd1ce408da448bd6ed97944e7
ACR-056e69d78c794c0ab56c818c1cb2eaef
ACR-a8d398d31bc548a8a6a21f02c747424f
ACR-e24ec44b1e9d4715a039367529e504eb
ACR-088cfe77d7784b18aa5a3d1d0a3ad9a5
ACR-46245abe91cd46c4868bd059e02ccf37
ACR-41c5bf5516b14fce95108b5668dd54aa
ACR-ecbab69bbbb24f9c82704035301baa96
ACR-044b8a0904d3494ebf9f5806f1bb38ac
ACR-881f6eb657d14d6897a15a7830d0eed8
ACR-69a89cd5648049bf9db4a5c9c5ea75e3
ACR-fb239a7ba8444ee9a3ff13acffaf7b2a
ACR-55a871c76800482788b7f0ceb201c1cd
ACR-337ac96bac694c959f03bbd5aa1cd905
ACR-955a6dd6528c42daa41d4b29ac1b5041
 */
package org.sonarsource.sonarlint.core.remediation.aicodefix;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.reporting.PreviouslyRaisedFindingsRepository;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFix;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixRepository;
import org.sonarsource.sonarlint.core.tracking.TaintVulnerabilityTrackingService;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/*ACR-306bc7b84252470e8f4f5bf018f84608
ACR-be5d451c33ec4735956c9d1155df59ad
ACR-438edf1f43dd4c2eab858b43a4b6bea8
 */
class AiCodeFixServiceTest {

  @RegisterExtension
  static SonarLintLogTester logTester = new SonarLintLogTester();

  @TempDir
  Path tempDir;

  private SonarLintDatabase db;

  @AfterEach
  void tearDown() {
    if (db != null) {
      db.shutdown();
    }
  }

  @Test
  void getFeature_reads_from_h2_repository() {
    db = new SonarLintDatabase(tempDir);
    var aiCodeFixRepo = new AiCodeFixRepository(db.dsl());

    var connectionId = "conn-1";
    var projectKey = "project-A";
    aiCodeFixRepo.upsert(new AiCodeFix(
      connectionId,
      Set.of("xml:S3421"),
      true,
      AiCodeFix.Enablement.ENABLED_FOR_ALL_PROJECTS,
      Set.of(projectKey)));

    var connectionRepository = mock(ConnectionConfigurationRepository.class);
    var configurationRepository = mock(ConfigurationRepository.class);
    var sonarQubeClientManager = mock(SonarQubeClientManager.class);
    var previouslyRaisedFindingsRepository = mock(PreviouslyRaisedFindingsRepository.class);
    var clientFileSystemService = mock(ClientFileSystemService.class);
    var eventPublisher = mock(ApplicationEventPublisher.class);
    var taintService = mock(TaintVulnerabilityTrackingService.class);

    var service = new AiCodeFixService(connectionRepository, configurationRepository, sonarQubeClientManager, previouslyRaisedFindingsRepository, clientFileSystemService,
      eventPublisher, taintService, aiCodeFixRepo);

    var binding = new Binding(connectionId, projectKey);

    Optional<AiCodeFixFeature> featureOpt = service.getFeature(binding);

    assertThat(featureOpt).isPresent();
    var feature = featureOpt.get();
    assertThat(feature.settings().supportedRules()).contains("xml:S3421");
    assertThat(feature.settings().isFeatureEnabled(projectKey)).isTrue();
  }
}
