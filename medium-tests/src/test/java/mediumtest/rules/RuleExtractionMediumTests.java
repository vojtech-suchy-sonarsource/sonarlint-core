/*
ACR-26bb364f944e4b658cb0fd28b21cb311
ACR-25cf7c50ebf54098b03cb9f78c01f1cd
ACR-ea3cc0f1b4aa4523b39cf55dc85857be
ACR-af5b61a0b34944ef9765b3a5d9363ee5
ACR-e1919629781943d8b5ccaf647db28092
ACR-83c060831bfd49ccac6d04741dbadabb
ACR-8dac80995a1943809a43d01240d23c31
ACR-375531bbdadb4350a87506504a44ab55
ACR-f559281ee4dd4cf1b22ac7e9704e5b66
ACR-03be329c264348a789ed89ae8021fc3c
ACR-2cfebb9884d54c82a356a3c926c63a09
ACR-97b8d70a7e9f4ca68f242db2e2483d36
ACR-05e24e36d85d4600ad72cc6ae4141fe8
ACR-bc7db4bffd6347af816fcde7760f1a46
ACR-6da43a1125bb4d37a762ffe09baf47b4
ACR-53708a32f88241cfb9346e427e4d1ad9
ACR-7e00059dfa554376a54207c67e93b3d2
 */
package mediumtest.rules;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.test.utils.plugins.SonarPluginBuilder.newSonarPlugin;

public class RuleExtractionMediumTests {

  @SonarLintTest
  void should_gracefully_skip_plugin_when_rules_definitions_fail(SonarLintTestHarness harness, @TempDir Path baseDir) throws ExecutionException, InterruptedException {
    var client = harness.newFakeClient()
      .build();

    //ACR-4e70ac3cada5456bb8498a0eccadc4f1
    var okPluginDir = baseDir.resolve("ok-plugin");
    okPluginDir.toFile().mkdirs();
    var okPluginPath = newSonarPlugin("ok-plugin")
      .withRulesDefinition(OkRulesDefinition.class)
      .generate(okPluginDir);

    //ACR-2275c3dccffd4966931585c5170f0e4c
    var throwingPluginDir = baseDir.resolve("throwing-plugin");
    throwingPluginDir.toFile().mkdirs();
    var throwingPluginPath = newSonarPlugin("throwing-plugin")
      .withRulesDefinition(ThrowingRulesDefinition.class)
      .generate(throwingPluginDir);

    var backend = harness.newBackend()
      .withStandaloneEmbeddedPlugin(okPluginPath)
      .withStandaloneEmbeddedPlugin(throwingPluginPath)
      .withEnabledLanguageInStandaloneMode(Language.PHP)
      .start(client);

    var allRules = backend.getRulesService().listAllStandaloneRulesDefinitions().get();

    assertThat(allRules.getRulesByKey()).containsOnlyKeys("ok-rules:S001");
  }

}
