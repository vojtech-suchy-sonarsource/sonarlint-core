/*
ACR-db2f2541d74e45438cc292978b606191
ACR-0254cf34aae8450fba4daa899109d63c
ACR-74ad736c05124a12af39ce6cfeebfdfd
ACR-036b59369aa349689a5de7d7c1e1b8d5
ACR-036a3673236b4db480887ee8e0a0f73f
ACR-9ff4c063b44c49ada0db781ea2699469
ACR-f41638aff567443a883dbf6466f00441
ACR-35e5894f2bb24869aedeafdb29421fda
ACR-6817ecf1b2af48419439364a5aff0f2b
ACR-cc3d190812824c3a88fbeeacfa11961b
ACR-89336142e2e8438e8b2a03683410fa46
ACR-7b2c3a9d24c44c9bb34b7702413b690b
ACR-721f4435b561459e87bf42bdae88a8c5
ACR-ec2f70be4940422a99439517770626c3
ACR-c348699bbae040fda329e0b3dee3ac3d
ACR-2ccff3d6b23347dca5b1455f4da31338
ACR-2623bc5bdee2444ab91a2441129b2643
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

/*ACR-9d907e3e5c9a4d4ba3860df39ed4fdd8
ACR-e51888a1f6fa4312bcfd58622ed9b1f8
ACR-acbd3d3f88e84342b48898c630bab5f8
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
