/*
ACR-d96c92b64adb4cec87582666595c2c27
ACR-95a517b1e9e048319b0abaa5ece1852f
ACR-6e5ea376ce8e40b08c480f5ccd19a04c
ACR-3b05308322cd4a21bd85ccd6b5a7bfb9
ACR-5555e3af055141b189f640b44e94b804
ACR-d2afd8b63702497e8dcef6b193e5b7e7
ACR-54eca17b49024aa4b1f3d275e74ec338
ACR-96b278507c9a424883f329965c6d195e
ACR-d7d891929f974dd89c7e42c500595da4
ACR-3a656a603e4644358b510ff95c1d2e10
ACR-1e4a307bb6fb458fbdb41c9aa67e3953
ACR-31f55ce09c8e461eb46b9dd8390d7a5a
ACR-e889578d2c994fe887016c38e6dd2deb
ACR-97b4ead3606c483f837a1950011a28c2
ACR-b437670c1e9b48eeb74f8943913c9235
ACR-c5dc5ccc16d34b76bcb269fb26f08ceb
ACR-0964b07632d047748ca9584d7643c630
 */
package mediumtest.analysis;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;
import static org.sonarsource.sonarlint.core.test.utils.plugins.SonarPluginBuilder.newSonarPlugin;
import static utils.AnalysisUtils.createFile;

class RulesInConnectedModeMediumTests {

  private static final String CONFIG_SCOPE_ID = "config scope id";
  private static final String CONNECTION_ID = "myConnection";
  private static final String JAVA_MODULE_KEY = "sample-project";

  @SonarLintTest
  void should_ignore_unknown_active_rule_parameters_and_convert_deprecated_keys(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "Class.java", "");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var activeRulesDumpingPlugin = newSonarPlugin("php")
      .withSensor(ActiveRulesDumpingSensor.class)
      .generate(baseDir);
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, harness.newFakeSonarQubeServer().start())
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, JAVA_MODULE_KEY)
      .withExtraEnabledLanguagesInConnectedMode(Language.JAVA)
      .withExtraEnabledLanguagesInConnectedMode(Language.PHP)
      .withStorage(CONNECTION_ID, s -> s
        .withPlugin(TestPlugin.JAVA)
        .withPlugin("php", activeRulesDumpingPlugin, "hash")
        .withProject(JAVA_MODULE_KEY, project -> project
          .withMainBranch("main")
          .withRuleSet("java", ruleSet -> ruleSet
            //ACR-c2479587617a41ce9ac0af2b0fbdb31f
            .withActiveRule("squid:S106", "BLOCKER")
            .withActiveRule("java:S3776", "BLOCKER", Map.of("blah", "blah"))
            //ACR-c02a0df6f9894951868c5985412fbcdb
            .withCustomActiveRule("squid:myCustomRule", "squid:S124", "MAJOR", Map.of("message", "Needs to be reviewed", "regularExpression", ".*REVIEW.*")))))
      .start(client);

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, UUID.randomUUID(), List.of(fileUri), Map.of(), false, System.currentTimeMillis()));

    await().atMost(3, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(baseDir.resolve("activerules.dump")).content()
        .contains("java:S106;java;null;")
        .contains("java:S3776;java;null;{Threshold=15}")
        .contains("java:myCustomRule;java;S124;{message=Needs to be reviewed, regularExpression=.*REVIEW.*}"));
  }

  @SonarLintTest
  void hotspot_rules_should_be_active_when_feature_flag_is_enabled(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "Class.java", "");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var activeRulesDumpingPlugin = newSonarPlugin("php")
      .withSensor(ActiveRulesDumpingSensor.class)
      .generate(baseDir);
    var backend = harness.newBackend()
      .withBackendCapability(SECURITY_HOTSPOTS)
      .withSonarQubeConnection(CONNECTION_ID, harness.newFakeSonarQubeServer().start())
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, JAVA_MODULE_KEY)
      .withExtraEnabledLanguagesInConnectedMode(Language.JAVA)
      .withExtraEnabledLanguagesInConnectedMode(Language.PHP)
      .withStorage(CONNECTION_ID,
        s -> s
          .withServerVersion("9.7")
          .withPlugin(TestPlugin.JAVA)
          .withPlugin("php", activeRulesDumpingPlugin, "hash")
          .withProject(JAVA_MODULE_KEY, project -> project
            .withMainBranch("main")
            .withRuleSet("java", ruleSet -> ruleSet
              .withActiveRule("java:S4792", "INFO"))))
      .start(client);

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, UUID.randomUUID(), List.of(fileUri), Map.of(), false, System.currentTimeMillis()));

    await().atMost(3, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(baseDir.resolve("activerules.dump")).content()
        .contains("java:S4792;java;null;"));
  }

  @SonarLintTest
  void hotspot_rules_should_not_be_active_when_feature_flag_is_disabled(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "Class.java", "");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var activeRulesDumpingPlugin = newSonarPlugin("php")
      .withSensor(ActiveRulesDumpingSensor.class)
      .generate(baseDir);
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, harness.newFakeSonarQubeServer().start())
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, JAVA_MODULE_KEY)
      .withExtraEnabledLanguagesInConnectedMode(Language.JAVA)
      .withExtraEnabledLanguagesInConnectedMode(Language.PHP)
      .withStorage(CONNECTION_ID,
        s -> s
          .withServerVersion("9.7")
          .withPlugin("php", activeRulesDumpingPlugin, "hash")
          .withProject(JAVA_MODULE_KEY, project -> project
            .withMainBranch("main")
            .withRuleSet("java", ruleSet -> ruleSet
              .withActiveRule("java:S4792", "INFO"))))
      .start(client);

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, UUID.randomUUID(), List.of(fileUri), Map.of(), false, System.currentTimeMillis()));

    await().atMost(3, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(baseDir.resolve("activerules.dump")).content()
        .doesNotContain("java:S4792;java;null;"));
  }

  @SonarLintTest
  void should_use_ipython_standalone_active_rules_in_connected_mode(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "mod.py", "");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false,
        null, filePath, null, null, true)))
      .build();
    var activeRulesDumpingPlugin = newSonarPlugin("php")
      .withSensor(ActiveRulesDumpingSensor.class)
      .generate(baseDir);
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPlugin(TestPlugin.PYTHON)
      .withEnabledLanguageInStandaloneMode(Language.IPYTHON)
      .withExtraEnabledLanguagesInConnectedMode(Language.PHP)
      .withSonarQubeConnection(CONNECTION_ID, harness.newFakeSonarQubeServer().start())
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, JAVA_MODULE_KEY)
      .withStorage(CONNECTION_ID,
        s -> s
          .withServerVersion("9.7")
          .withPlugin("php", activeRulesDumpingPlugin, "hash")
          .withProject(JAVA_MODULE_KEY, project -> project
            .withMainBranch("main")
            .withRuleSet("java", ruleSet -> ruleSet
              .withActiveRule("java:S4792", "INFO"))))
      .start(client);

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, UUID.randomUUID(), List.of(fileUri), Map.of(), false, System.currentTimeMillis()));

    await().atMost(3, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(baseDir.resolve("activerules.dump")).content()
        .contains("ipython:PrintStatementUsage"));
  }

}
