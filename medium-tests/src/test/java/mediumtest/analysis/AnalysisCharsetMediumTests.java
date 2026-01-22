/*
ACR-29c551f643fb474e8196591ebd30ee31
ACR-db4df541084f4cb592e2efe0d91d96dc
ACR-07a984a724be499088309569c9fbad96
ACR-58491ed001da4ac3a42681453eb0823a
ACR-0783ea64191b4f3ebb79f7d47c0da8cb
ACR-d47b3431eece40fdaaee58027458758a
ACR-6cb024e3e37e4f23beabb6868313a1d0
ACR-1fee6c9b614445d08c2038325b9a61e9
ACR-c3f67d46eb4443a8afca06d2b6a6a984
ACR-edb050fb119d4259bc01e0604ee33c5f
ACR-076a1267a663412ba93126f6efde4d9c
ACR-9ef0e12538b240cf9d11d8c0c3bb08d5
ACR-9c7613a6f8084fd485db943c1ac19487
ACR-5b505da630204baf8110305a27b2170a
ACR-eacf4e6e9bc04d46956ca3918154dd1c
ACR-b5d639057598405e944d57ca0d1978bc
ACR-efd67cda1b964f7d939490b422c063a7
 */
package mediumtest.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ImpactDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static utils.AnalysisUtils.awaitRaisedIssuesNotification;

class AnalysisCharsetMediumTests {

  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  @SonarLintTest
  void it_should_skip_utf8_bom_when_reading_from_disk(SonarLintTestHarness harness, @TempDir Path baseDir) throws IOException {
    var filePath = baseDir.resolve("file.js");
    Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/file-with-utf8-bom.js")), filePath);
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)
      .start(client);
    var analysisId = UUID.randomUUID();

    var result = backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(fileUri), Map.of(), false, System.currentTimeMillis())).join();

    assertThat(result.getFailedAnalysisFiles()).isEmpty();
    var raisedIssueDto = awaitRaisedIssuesNotification(client, CONFIG_SCOPE_ID).get(0);
    assertThat(raisedIssueDto.getSeverityMode().isRight()).isTrue();
    assertThat(raisedIssueDto.getSeverityMode().getRight().getCleanCodeAttribute()).isEqualTo(CleanCodeAttribute.COMPLETE);
    assertThat(raisedIssueDto.getSeverityMode().getRight().getImpacts())
      .extracting(ImpactDto::getSoftwareQuality, ImpactDto::getImpactSeverity)
      .containsExactly(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.INFO));
    assertThat(raisedIssueDto.getRuleKey()).isEqualTo("javascript:S1135");
    assertThat(raisedIssueDto.getPrimaryMessage()).isEqualTo("Complete the task associated to this \"TODO\" comment.");
    assertThat(raisedIssueDto.getFlows()).isEmpty();
    assertThat(raisedIssueDto.getQuickFixes()).isEmpty();
    assertThat(raisedIssueDto.getTextRange()).usingRecursiveComparison().isEqualTo(new TextRangeDto(1, 3, 1, 7));
    assertThat(raisedIssueDto.getRuleDescriptionContextKey()).isNull();
  }
}
