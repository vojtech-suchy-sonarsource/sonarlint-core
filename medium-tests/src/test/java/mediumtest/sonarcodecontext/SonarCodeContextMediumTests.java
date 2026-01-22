/*
ACR-55bd54f474554142bc2c36064118775b
ACR-75fd003849fe4b33b54135837d473886
ACR-b277c3f98e2c4874ab4a0e2043f3d579
ACR-3aa9e2a6e7ce4dcab3ca776fdfc88ecd
ACR-da9467f6ba294e7390c1d1043d2124ff
ACR-f134b3673e684ceca008f0870e41482e
ACR-6cecaf8424204310ae06e6dfb1469284
ACR-c228518026014885aeec4515ff6647de
ACR-d3ef3e9f4cba4fe1b340cc4b9913aa03
ACR-b899bfb7e9f0433ba78035da9e641852
ACR-ea081975622541e1a668cda832856cdf
ACR-1b9bc564ae8c49f59f8962c147e6f219
ACR-6f80fe94c5ea4f01aa740763ca2484c8
ACR-de707d1da6494f66915039c00ad73eac
ACR-1620d6b446824b82b0b52685e06574c0
ACR-0aed43d29f9f4219a9f2ee71a63853fe
ACR-ab179a9a758241e293b22603099b9a9f
 */
package mediumtest.sonarcodecontext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import utils.TestPlugin;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.commons.dogfood.DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY;

@ExtendWith(SystemStubsExtension.class)
class SonarCodeContextMediumTests {

  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";
  private static final String CONNECTION_ID = "connectionId";
  private static final String PROJECT_KEY = "projectKey";

  @SystemStub
  private EnvironmentVariables environmentVariables;

  @BeforeEach
  void clearDogfoodFlag() {
    environmentVariables.remove(SONARSOURCE_DOGFOODING_ENV_VAR_KEY);
  }

  @AfterEach
  void cleanUp() {
    environmentVariables.remove(SONARSOURCE_DOGFOODING_ENV_VAR_KEY);
  }

  @SonarLintTest
  //ACR-981e00346fc34e10938f24123f48c097
  @DisabledOnOs(OS.WINDOWS)
  void should_regenerate_on_binding_change(SonarLintTestHarness harness, @TempDir Path baseDir, @TempDir Path binDir)
    throws IOException {
    var cliPath = createFakeCli(binDir);
    System.setProperty("sonar.code.context.executable", cliPath.toString());
    environmentVariables.set(SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "1");

    var filePath = baseDir.resolve("Foo.java");
    Files.writeString(filePath, "public class Foo {}", UTF_8);
    var fileDto = new ClientFileDto(filePath.toUri(), baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true);

    var client = harness.newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(fileDto))
      .build();

    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY)
      .start();

    var backend = harness.newBackend()
      .withBackendCapability(BackendCapability.CONTEXT_GENERATION)
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage
        .withPlugin(TestPlugin.JAVA)
        .withProject(PROJECT_KEY, p -> p.withMainBranch("main")))
      .start(client);

    //ACR-1cf04854dcff4ca6ae5ada05d944fd1d
    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
      new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
        new BindingConfigurationDto(CONNECTION_ID, PROJECT_KEY, true)))));

    var sonarMd = baseDir.resolve(".sonar-code-context").resolve("SONAR.md");
    await().untilAsserted(() -> assertThat(Files.exists(sonarMd)).isTrue());

    //ACR-803c31308f8644939e1525587897bafe
    Files.deleteIfExists(sonarMd);
    assertThat(Files.exists(sonarMd)).isFalse();

    var newProjectKey = PROJECT_KEY + "-updated";
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(CONFIG_SCOPE_ID,
      new BindingConfigurationDto(CONNECTION_ID, newProjectKey, true), null, null));

    await().untilAsserted(() -> assertThat(Files.exists(sonarMd)).isTrue());
  }

  @SonarLintTest
  //ACR-f7df24ed57a644ba98b4017afe572c71
  @DisabledOnOs(OS.WINDOWS)
  void should_generate_sonar_md_and_mdc_on_bound_scope_when_dogfooding(SonarLintTestHarness harness, @TempDir Path baseDir, @TempDir Path binDir)
    throws IOException {
    //ACR-c385c2e4137646619188799425848f4a
    var cliPath = createFakeCli(binDir);
    //ACR-f05195c852834e7d85d0f8ab10ec755e
    System.setProperty("sonar.code.context.executable", cliPath.toString());
    environmentVariables.set(SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "1");

    var filePath = baseDir.resolve("Foo.java");
    Files.writeString(filePath, "public class Foo {}", UTF_8);
    var fileDto = new ClientFileDto(filePath.toUri(), baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true);

    var client = harness.newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(fileDto))
      .build();

    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY)
      .start();

    var backend = harness.newBackend()
      .withBackendCapability(BackendCapability.CONTEXT_GENERATION)
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage
        .withPlugin(TestPlugin.JAVA)
        .withProject(PROJECT_KEY, p -> p.withMainBranch("main")))
      .start(client);

    //ACR-0628168ca9e6466cbac8f209737be290
    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
      new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
        new BindingConfigurationDto(CONNECTION_ID, PROJECT_KEY, true)))));

    var sonarMd = baseDir.resolve(".sonar-code-context").resolve("SONAR.md");
    var mdc = baseDir.resolve(".cursor").resolve("rules").resolve("sonar-code-context.mdc");
    await().untilAsserted(() -> assertThat(Files.exists(sonarMd)).isTrue());
    await().untilAsserted(() -> assertThat(Files.exists(mdc)).isTrue());
    var mdContent = Files.readString(sonarMd);
    assertThat(mdContent).contains("SONAR.md");
    var mdcContent = Files.readString(mdc);
    assertThat(mdcContent).contains("sonar-code-context.mdc");
    assertThat(Files.isExecutable(cliPath)).isTrue();
  }

  @SonarLintTest
  void should_not_generate_files_when_not_dogfooding(SonarLintTestHarness harness, @TempDir Path baseDir, @TempDir Path binDir)
    throws IOException {
    //ACR-4242e3be63624c79a1ed02ec643290aa
    var cliPath = createFakeCli(binDir);
    System.setProperty("sonar.code.context.executable", cliPath.toString());

    var filePath = baseDir.resolve("Foo.java");
    Files.writeString(filePath, "public class Foo {}", UTF_8);
    var fileDto = new ClientFileDto(filePath.toUri(), baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true);

    var client = harness.newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(fileDto))
      .build();

    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY)
      .start();

    var backend = harness.newBackend()
      .withBackendCapability(BackendCapability.CONTEXT_GENERATION)
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage
        .withPlugin(TestPlugin.JAVA)
        .withProject(PROJECT_KEY, p -> p.withMainBranch("main")))
      .start(client);

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
      new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
        new BindingConfigurationDto(CONNECTION_ID, PROJECT_KEY, true)))));

    var sonarMd = baseDir.resolve(".sonar-code-context").resolve("SONAR.md");
    var mdc = baseDir.resolve(".cursor").resolve("rules").resolve("sonar-code-context.mdc");
    await().during(java.time.Duration.ofMillis(300)).untilAsserted(() -> {
      assertThat(Files.exists(sonarMd)).isFalse();
      assertThat(Files.exists(mdc)).isFalse();
    });
  }

  @SonarLintTest
  void should_not_generate_when_dogfood_enabled_but_capability_missing(SonarLintTestHarness harness, @TempDir Path baseDir, @TempDir Path binDir)
    throws IOException {
    var cliPath = createFakeCli(binDir);
    System.setProperty("sonar.code.context.executable", cliPath.toString());
    environmentVariables.set(SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "1");

    var filePath = baseDir.resolve("Foo.java");
    Files.writeString(filePath, "public class Foo {}", UTF_8);
    var fileDto = new ClientFileDto(filePath.toUri(), baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true);

    var client = harness.newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(fileDto))
      .build();

    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY)
      .start();

    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage
        .withPlugin(TestPlugin.JAVA)
        .withProject(PROJECT_KEY, p -> p.withMainBranch("main")))
      .start(client);

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
      new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
        new BindingConfigurationDto(CONNECTION_ID, PROJECT_KEY, true)))));

    var sonarMd = baseDir.resolve(".sonar-code-context").resolve("SONAR.md");
    var mdc = baseDir.resolve(".cursor").resolve("rules").resolve("sonar-code-context.mdc");
    await().during(java.time.Duration.ofMillis(300)).untilAsserted(() -> {
      assertThat(Files.exists(sonarMd)).isFalse();
      assertThat(Files.exists(mdc)).isFalse();
    });
  }

  private Path createFakeCli(Path binDir) throws IOException {
    Files.createDirectories(binDir);
    var cli = binDir.resolve("sonar-code-context");
    var content = """
      #!/usr/bin/env bash
      set -e
      cmd="$1"
      shift
      mkdir -p .sonar-code-context
      if [ "$cmd" = "init" ]; then
        echo '{"version":1}' > .sonar-code-context/settings.json
      elif [ "$cmd" = "generate-md-guidelines" ]; then
        echo "SONAR_GUIDELINES generated $*" > .sonar-code-context/SONAR_GUIDELINES.md
      elif [ "$cmd" = "merge-md" ]; then
        echo "SONAR.md merged" > .sonar-code-context/SONAR.md
      elif [ "$cmd" = "install" ]; then
        mkdir -p .cursor/rules
        echo "sonar-code-context.mdc generated $*" > .cursor/rules/sonar-code-context.mdc
      fi""";
    Files.writeString(cli, content, UTF_8);
    try {
      Files.setPosixFilePermissions(cli, Set.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
    } catch (UnsupportedOperationException e) {
      //ACR-d4d769a403d14a32b3a3fedd0caf061f
    }
    return cli;
  }

}


