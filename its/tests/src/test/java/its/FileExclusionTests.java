/*
ACR-e389a9935a7a4376a366f10e7bdaea1a
ACR-84bad02712cb47769ac43e374823c94c
ACR-459c53d95da044f28b0865e13cbf7932
ACR-61dd4a04f04248ed9ceb41d38960bfac
ACR-e8300eec60e5440ea55f4be688cd4330
ACR-03b913b886724a04a0233c92a0d5e0c9
ACR-41fe05e65c4740c38c69df316375d922
ACR-8578a04ce82e4e39b5bda25c24d5a728
ACR-e760392429494c03854d261dcc26821f
ACR-a0ec27a750274738afd71a39ee273d27
ACR-04064c9a0cb0412883780f38af321226
ACR-857b6fe0acd64de5bbc9a3b294e0d201
ACR-40813cadf869429f83eb5457efbd6a56
ACR-dbd52e69e21f46f08020420a0e2ccf47
ACR-b950f7d95e6e46599f4b7025190fb812
ACR-71e2efb6966b4f9fba302211436b2669
ACR-d5b64e4d2c5f41a09a4f403fc6aa0f5e
 */
package its;

import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.locator.FileLocation;
import its.utils.OrchestratorUtils;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.settings.ResetRequest;
import org.sonarqube.ws.client.settings.SetRequest;
import org.sonarqube.ws.client.users.CreateRequest;
import org.sonarsource.sonarlint.core.rpc.client.ClientJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.client.ConnectionNotFoundException;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.impl.BackendJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.GetFilesStatusParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;

class FileExclusionTests extends AbstractConnectedTests {
  @RegisterExtension
  static OrchestratorExtension ORCHESTRATOR = OrchestratorUtils.defaultEnvBuilder()
    .addPlugin(FileLocation.of("../plugins/java-custom-rules/target/java-custom-rules-plugin.jar"))
    .setServerProperty("sonar.projectCreation.mainBranchName", MAIN_BRANCH_NAME)
    .build();

  private static final String CONNECTION_ID = "orchestrator";

  @TempDir
  private static Path sonarUserHome;
  private static WsClient adminWsClient;
  private static SonarLintRpcServer backend;
  private static final Map<String, Boolean> analysisReadinessByConfigScopeId = new ConcurrentHashMap<>();
  private static BackendJsonRpcLauncher serverLauncher;

  @BeforeAll
  static void startBackend() throws IOException {
    System.setProperty("sonarlint.internal.synchronization.initialDelay", "3");
    System.setProperty("sonarlint.internal.synchronization.period", "5");
    System.setProperty("sonarlint.internal.synchronization.scope.period", "3");

    var clientToServerOutputStream = new PipedOutputStream();
    var clientToServerInputStream = new PipedInputStream(clientToServerOutputStream);

    var serverToClientOutputStream = new PipedOutputStream();
    var serverToClientInputStream = new PipedInputStream(serverToClientOutputStream);

    serverLauncher = new BackendJsonRpcLauncher(clientToServerInputStream, serverToClientOutputStream);
    var clientLauncher = new ClientJsonRpcLauncher(serverToClientInputStream, clientToServerOutputStream, newDummySonarLintClient());

    backend = clientLauncher.getServerProxy();
    try {
      var enabledLanguages = Set.of(JAVA);
      backend.initialize(
        new InitializeParams(IT_CLIENT_INFO,
          IT_TELEMETRY_ATTRIBUTES, HttpConfigurationDto.defaultConfig(), null, Set.of(BackendCapability.FULL_SYNCHRONIZATION, BackendCapability.PROJECT_SYNCHRONIZATION),
          sonarUserHome.resolve("storage"),
          sonarUserHome.resolve("work"),
          Collections.emptySet(), Collections.emptyMap(), enabledLanguages, Collections.emptySet(), Collections.emptySet(),
          List.of(new SonarQubeConnectionConfigurationDto(CONNECTION_ID, ORCHESTRATOR.getServer().getUrl(), true)),
          Collections.emptyList(),
          sonarUserHome.toString(),
          Map.of(), false, null, false, null))
        .get();
    } catch (Exception e) {
      throw new IllegalStateException("Cannot initialize the backend", e);
    }

    adminWsClient = newAdminWsClient(ORCHESTRATOR);
    adminWsClient.users().create(new CreateRequest().setLogin(SONARLINT_USER).setPassword(SONARLINT_PWD).setName("SonarLint"));
  }

  @AfterAll
  static void stop() throws ExecutionException, InterruptedException {
    backend.shutdown().get();
    System.clearProperty("sonarlint.internal.synchronization.initialDelay");
    System.clearProperty("sonarlint.internal.synchronization.period");
    System.clearProperty("sonarlint.internal.synchronization.scope.period");
  }

  @AfterEach
  void cleanup_after_each() {
    analysisReadinessByConfigScopeId.clear();
    rpcClientLogs.clear();
  }

  @Test
  void should_respect_exclusion_settings_on_SQ() {
    var configScopeId = "should_respect_exclusion_settings_on_SQ";
    var projectKey = "sample-java";
    var projectName = "my-sample-java";
    provisionProject(ORCHESTRATOR, projectKey, projectName);

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(
      List.of(new ConfigurationScopeDto(configScopeId, null, true, projectName, new BindingConfigurationDto(CONNECTION_ID, projectKey,
        true)))));
    await().atMost(1, MINUTES).untilAsserted(() -> assertThat(analysisReadinessByConfigScopeId).containsEntry(configScopeId, true));

    var filePath = Path.of("src/main/java/foo/Foo.java");
    var clientFileDto = new ClientFileDto(filePath.toUri(), filePath, configScopeId, null, StandardCharsets.UTF_8.name(),
      filePath.toAbsolutePath(), null, null, true);
    var didUpdateFileSystemParams = new DidUpdateFileSystemParams(List.of(clientFileDto), List.of(), List.of());
    backend.getFileService().didUpdateFileSystem(didUpdateFileSystemParams);

    //ACR-5f412d2689814e76aeb14fcbb7041a92
    var getFilesStatusParams = new GetFilesStatusParams(Map.of(configScopeId, List.of(filePath.toUri())));
    await().atMost(10, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isFalse());

    //ACR-b9828b7bc44b4b68aa6e6a495141cf60
    adminWsClient.settings().set(new SetRequest()
      .setKey("sonar.exclusions")
      .setValues(singletonList("**/*.java"))
      .setComponent(projectKey));

    forceBackendToPullSettings(configScopeId, projectKey);

    //ACR-35ee269365304574bf8ee330abb695b0
    await().atMost(30, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isTrue());

    //ACR-e1d4de25d6f245a6bcaa46e989dbe433
    adminWsClient.settings().set(new SetRequest()
      .setKey("sonar.exclusions")
      .setValues(singletonList("**/*.js"))
      .setComponent(projectKey));

    forceBackendToPullSettings(configScopeId, projectKey);

    //ACR-39323744639b42c4adf9b31382ddf933
    await().atMost(30, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isFalse());

    //ACR-3d45e8bd6f9044afbbf6e067aecf1edf
    adminWsClient.settings().set(new SetRequest()
      .setKey("sonar.inclusions")
      .setValues(singletonList("**/*.js"))
      .setComponent(projectKey));

    forceBackendToPullSettings(configScopeId, projectKey);

    //ACR-dd555ba1427b4a86b72379deb4099448
    await().atMost(30, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isTrue());

    //ACR-5d1b1346962c47a494bf188d83eb3e46
    adminWsClient.settings().reset(new ResetRequest()
      .setKeys(List.of("sonar.exclusions", "sonar.inclusions"))
      .setComponent(projectKey));

    forceBackendToPullSettings(configScopeId, projectKey);

    //ACR-680bc9b40cf6415f89bb37fada112272
    await().atMost(30, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isFalse());
  }

  private static void forceBackendToPullSettings(String configScopeId, String projectKey) {
    //ACR-e2055de8b7b04a679b49bdaa5e18ce3e
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(configScopeId, new BindingConfigurationDto(null, null, true)));
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(configScopeId, new BindingConfigurationDto(CONNECTION_ID, projectKey, true)));
  }

  private static SonarLintRpcClientDelegate newDummySonarLintClient() {
    return new MockSonarLintRpcClientDelegate() {

      @Override
      public Either<TokenDto, UsernamePasswordDto> getCredentials(String connectionId) throws ConnectionNotFoundException {
        if (connectionId.equals(CONNECTION_ID)) {
          return Either.forRight(new UsernamePasswordDto(SONARLINT_USER, SONARLINT_PWD));
        }
        return super.getCredentials(connectionId);
      }

      @Override
      public void didChangeAnalysisReadiness(Set<String> configurationScopeIds, boolean areReadyForAnalysis) {
        analysisReadinessByConfigScopeId.putAll(configurationScopeIds.stream().collect(Collectors.toMap(Function.identity(), k -> areReadyForAnalysis)));
      }

      @Override
      public void log(LogParams params) {
        System.out.println(params);
        rpcClientLogs.add(params);
      }
    };
  }
}
