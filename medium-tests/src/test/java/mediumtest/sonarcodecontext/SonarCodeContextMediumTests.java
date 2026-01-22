/*
ACR-62877e54944c4b81afd6b2feee5746c3
ACR-9fa87014a8074fada1c024f6456aa88d
ACR-acba27afa32549d7893a98782abde53f
ACR-08ddfd5d70314d27aa6a9e10f7604c28
ACR-21ad4582666e4aee89ca999658c5cc06
ACR-b64b130a54704f7db0ffe0c552beca91
ACR-3e98b3c54d5e41d49bd496ebf4a5e7f3
ACR-184e6dcd79bb40799a1e6c273a661d9e
ACR-a6295b09c586471ba0ff9677b8512097
ACR-c416cf6477fb445ca7cefe92df476423
ACR-9d95ff229dcb4b4ebafd33182f060f63
ACR-492581ad69014e57bd9828b1b67e75ea
ACR-387a2bbde5264490b87223805d7db2c5
ACR-ea64990924974c66ba5c100ae520ce11
ACR-4aac5c36f40e436dab7d70ab31ac3c8f
ACR-b290e478494b4ccf90b08235a4032221
ACR-2ac219aae9464e9e94281caa03d6f02d
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
  //ACR-5ba7db074db34d4a9434033518deabf3
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

    //ACR-83033c6b7fef4a5eb0def6a39f6ccc4b
    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
      new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, CONFIG_SCOPE_ID,
        new BindingConfigurationDto(CONNECTION_ID, PROJECT_KEY, true)))));

    var sonarMd = baseDir.resolve(".sonar-code-context").resolve("SONAR.md");
    await().untilAsserted(() -> assertThat(Files.exists(sonarMd)).isTrue());

    //ACR-73c91a1e68b9421b8cd4490287c2fd1e
    Files.deleteIfExists(sonarMd);
    assertThat(Files.exists(sonarMd)).isFalse();

    var newProjectKey = PROJECT_KEY + "-updated";
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(CONFIG_SCOPE_ID,
      new BindingConfigurationDto(CONNECTION_ID, newProjectKey, true), null, null));

    await().untilAsserted(() -> assertThat(Files.exists(sonarMd)).isTrue());
  }

  @SonarLintTest
  //ACR-b7f1c5df63c34bc29b79cc3a6045fa9c
  @DisabledOnOs(OS.WINDOWS)
  void should_generate_sonar_md_and_mdc_on_bound_scope_when_dogfooding(SonarLintTestHarness harness, @TempDir Path baseDir, @TempDir Path binDir)
    throws IOException {
    //ACR-4d632c7b59c54722948dd84c674a0ec8
    var cliPath = createFakeCli(binDir);
    //ACR-507ae39de49f424f830ea7a2401148af
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

    //ACR-043ada6b728a4adcbfb47f8e037bbc8d
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
    //ACR-9ccb3c2ff18545a08812d18406666273
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
      //ACR-d9480128a5704c29b7e9054a08534ccb
    }
    return cli;
  }

}


