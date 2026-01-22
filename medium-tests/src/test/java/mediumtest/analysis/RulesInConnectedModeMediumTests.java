/*
ACR-1eba241a8b4f4f089dd3822aa2d684d0
ACR-ca79b2d4884948808d09bd1580b2f296
ACR-5dbb703cec3b400eaa3a2d295c35cb8b
ACR-d81a653b03234169a0c60c50ca0f867a
ACR-7a73fe4c285c4cab8f91c9b6d6f8c648
ACR-f2cecbd546f34a7bb739865ab3f80a1a
ACR-e2b9be69b2a34c4c8889bbf69e0bb168
ACR-6e2d54e6f2e744b685c29f11d48c2764
ACR-73fade07ce984427862d8f6efb22bc6e
ACR-ad3abda6859f48de959d0d5688e72759
ACR-bf30fffac3e147e98d433dbc79f38588
ACR-2bf7f903310442dba5d3d6022fa9532f
ACR-34f94ff770374fadba7ccb2e46b36f70
ACR-0c3df46f5d2b4bd9930e413c7e791421
ACR-286d93f74b64452383acd80575301cd3
ACR-f2e9bc524a9a4489ad36d6436afe8d55
ACR-415675302dc84f37bb312c7f585d57e0
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
            //ACR-88f3df6689534db8819ff3f61922b1af
            .withActiveRule("squid:S106", "BLOCKER")
            .withActiveRule("java:S3776", "BLOCKER", Map.of("blah", "blah"))
            //ACR-c4eee3bbcab040e78328755e63f4dadf
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
