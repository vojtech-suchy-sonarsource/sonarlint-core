/*
ACR-77f0c52566df426e8431f428c22a87be
ACR-b18667299c804dc9a34a0a1130cadae9
ACR-f351c0c3792e47dd8aaee66c90d91df7
ACR-409133ccc6a54c988569681c09e04201
ACR-835cc525888a440b91439fe35ad836d9
ACR-bbd83fd036e54bd0a2cbf4888c693b51
ACR-12be2e2e9a824788a8f826a9880d1ea4
ACR-0a0a6b48795447eaa03855eae5f2dbd9
ACR-5f3ddcbb5345457c899e1b3ab6711b8d
ACR-e4adc3bc338943669634994e393f57a6
ACR-6d9de7a093574fedb46a80cf248112f4
ACR-c2fbd6f39527497d957d251f105f4272
ACR-ec54b190e7c34e6da63f9b942c9266c1
ACR-18c4ad7b3eb347cf865db58389db5d06
ACR-80abd285cb7743b48114e376556277aa
ACR-56cf95bc1c984d10a1822c6de46af7dc
ACR-94f6614cf01c47a7b01d33d9ed25ae73
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

    //ACR-395026f372cb4141a22aa248c4183964
    var getFilesStatusParams = new GetFilesStatusParams(Map.of(configScopeId, List.of(filePath.toUri())));
    await().atMost(10, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isFalse());

    //ACR-a9ee07b0e6484abc95bb76a115941570
    adminWsClient.settings().set(new SetRequest()
      .setKey("sonar.exclusions")
      .setValues(singletonList("**/*.java"))
      .setComponent(projectKey));

    forceBackendToPullSettings(configScopeId, projectKey);

    //ACR-6387be11cad24a4ebe56ba6eaae70ca4
    await().atMost(30, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isTrue());

    //ACR-542af29933a64f0f920ce1b88107aee3
    adminWsClient.settings().set(new SetRequest()
      .setKey("sonar.exclusions")
      .setValues(singletonList("**/*.js"))
      .setComponent(projectKey));

    forceBackendToPullSettings(configScopeId, projectKey);

    //ACR-bbde20bd4d6445f4bead24e145269fae
    await().atMost(30, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isFalse());

    //ACR-885f9c35af3a434e9e0ac15c2bcf13d9
    adminWsClient.settings().set(new SetRequest()
      .setKey("sonar.inclusions")
      .setValues(singletonList("**/*.js"))
      .setComponent(projectKey));

    forceBackendToPullSettings(configScopeId, projectKey);

    //ACR-80ccff87c5f4407c980cee2bc3b171a9
    await().atMost(30, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isTrue());

    //ACR-13f741effacf4b2895ed21540178c8a2
    adminWsClient.settings().reset(new ResetRequest()
      .setKeys(List.of("sonar.exclusions", "sonar.inclusions"))
      .setComponent(projectKey));

    forceBackendToPullSettings(configScopeId, projectKey);

    //ACR-781a91c624cc4400a210e71a1e2cf555
    await().atMost(30, SECONDS)
      .untilAsserted(() -> assertThat(backend.getFileService().getFilesStatus(getFilesStatusParams).get().getFileStatuses().get(filePath.toUri()).isExcluded()).isFalse());
  }

  private static void forceBackendToPullSettings(String configScopeId, String projectKey) {
    //ACR-2bf62797cede4204a8a9be00868ba94e
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
