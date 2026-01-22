/*
ACR-30165ea9002a43fdb59c7f8f551e43ed
ACR-7a73b6f161114b3fbf027fe6b81ac5fe
ACR-55a46a2640e442379f84a598c8e6492d
ACR-bbbd8a6d3dc54ec6a8f8eb867240de24
ACR-02bc8f5b35d34d1080104bac8f3f23c3
ACR-d876ccdd88e1485489137fb70c11897e
ACR-62dad0c959e8455a88b9c9a23c0e1398
ACR-984ea6b9d0294890b9cb7902e907105a
ACR-639ced966d4e4d5689d7e81449beca93
ACR-43ef1f89935546109f57edbc00fadfff
ACR-5cbc249c49fe4745989429708d807cfa
ACR-a9fb1708c12645b38a0a33ab20eb3a65
ACR-f4cf4cc50e7d4badbaaaf78603a38bb4
ACR-ca9222d4b6d943af9df2bd5b2864cbc9
ACR-b2a9c2507a5049329cba2090974aa2f9
ACR-63158e0ca9b449cf85dc450039851096
ACR-a9cf7053115f441c9d2c7cd472f66825
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
