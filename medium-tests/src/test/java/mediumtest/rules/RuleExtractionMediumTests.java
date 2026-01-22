/*
ACR-3975f4af35254c5dbf69f92b69265c8d
ACR-21e610c0d7d647fe8050840822055172
ACR-3e9db834a653475f9c33629a7ae5e38e
ACR-4803fad232b347b08e68dfce45ebef44
ACR-98614e601db34fa08b74fa28ea3c4de3
ACR-f241c92070124e138e4f7a2510603f53
ACR-4eafff0eefb842e98cee37bca93e02b2
ACR-7899334173034e1284e6eaa0be039017
ACR-0729c49494184e5ab6ff22ef76d86475
ACR-672f805c66d84bc3a606bc73dd802867
ACR-20dfb6b2ed15466195be2a1d9973708d
ACR-73978fae6eda489c87d7ae73a6242cb5
ACR-cc47c152f9354c1a8774ef0558006e33
ACR-a2c85785d5c749dd917307cc80bade7c
ACR-85efcff199e5438bac6764601c64c74f
ACR-71448065d4a1440f87402ecb86ead96d
ACR-47e280b7e5764f8bbc90b7a263cfdb9c
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

    //ACR-a92945ce530e41e185120c9ec9cf2d37
    var okPluginDir = baseDir.resolve("ok-plugin");
    okPluginDir.toFile().mkdirs();
    var okPluginPath = newSonarPlugin("ok-plugin")
      .withRulesDefinition(OkRulesDefinition.class)
      .generate(okPluginDir);

    //ACR-33b8f888157e4692ba0bff3b8a025e98
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
