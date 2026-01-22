/*
ACR-36b863f9219a450c8c87ebb997d0e532
ACR-57be56df79874a1aa38cc95a62056359
ACR-5efbbd579ef9424f91eb749b4816ce8f
ACR-a7ebb4764a5a4b5584585d1fe6ad7150
ACR-5e0ecdca5ca24f4c99eee4b1f1052dd6
ACR-63b4dfe9b359494cb51227c0965a35ff
ACR-9b7e945cdad34bb993fdcd7b43a823b3
ACR-736af17c291143f3a3e9c3595ecd8cf5
ACR-f73d6397ceef4e8ba815a136e40a8540
ACR-334a03403276424181716f413d55eb5f
ACR-8f747f8bda044aa498530d32bec7092b
ACR-9627b41c52e44e2fa83ed28aacab750f
ACR-2f6df6a862f14c9f8189cb87f524e372
ACR-677ed61c094142fb891475b62281697b
ACR-c71eef5a24d94236b04ea4a64f7cf465
ACR-275b681a68cd44be92a281aa12d4fe82
ACR-8c5c5c3204ae4a0c835cdfa44a640b10
 */
package its;

import com.sonar.orchestrator.container.Edition;
import com.sonar.orchestrator.junit5.OnlyOnSonarQube;
import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.version.Version;
import its.utils.OrchestratorUtils;
import its.utils.PluginLocator;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.permissions.RemoveGroupRequest;
import org.sonarqube.ws.client.settings.SetRequest;
import org.sonarqube.ws.client.users.CreateRequest;
import org.sonarsource.sonarlint.core.rpc.client.ClientJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.client.ConnectionNotFoundException;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.impl.BackendJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidRemoveConfigurationScopeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.CheckDependencyRiskSupportedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.APEX;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.C;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.COBOL;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.CPP;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JCL;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.SECRETS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.TSQL;

class SonarQubeEnterpriseEditionTests extends AbstractConnectedTests {
  public static final String CONNECTION_ID = "orchestrator";
  private static final String PROJECT_KEY_COBOL = "sample-cobol";
  private static final String PROJECT_KEY_JCL = "sample-jcl";
  private static final String PROJECT_KEY_C = "sample-c";
  private static final String PROJECT_KEY_TSQL = "sample-tsql";
  private static final String PROJECT_KEY_APEX = "sample-apex";
  private static final String PROJECT_KEY_CUSTOM_SECRETS = "sample-custom-secrets";
  private static final String PROJECT_KEY_MISRA = "sample-misra";
  private static final String PROJECT_KEY_SCA = "sample-sca";
  public static final String SONAR_EARLY_ACCESS_MISRA_ENABLED_PROPERTY_KEY = "sonar.earlyAccess.misra.enabled";
  public static final String SONAR_LEGACY_SCA_FEATURE_ENABLED_PROPERTY_KEY = "sonar.sca.enabled";
  public static final String SONAR_SCA_FEATURE_ENABLED_PROPERTY_KEY = "sonar.sca.featureEnabled";

  @RegisterExtension
  static OrchestratorExtension ORCHESTRATOR = OrchestratorUtils.defaultEnvBuilder()
    //ACR-4877d2610b184ede855ca258c6cf8f07
    .setServerProperty(SONAR_LEGACY_SCA_FEATURE_ENABLED_PROPERTY_KEY, "true")
    .setServerProperty(SONAR_SCA_FEATURE_ENABLED_PROPERTY_KEY, "true")
    .setServerProperty(SONAR_EARLY_ACCESS_MISRA_ENABLED_PROPERTY_KEY, "true")
    .setEdition(Edition.ENTERPRISE)
    .activateLicense()
    .restoreProfileAtStartup(FileLocation.ofClasspath("/c-sonarlint.xml"))
    .restoreProfileAtStartup(FileLocation.ofClasspath("/cobol-sonarlint.xml"))
    .restoreProfileAtStartup(FileLocation.ofClasspath("/jcl-sonarlint.xml"))
    .restoreProfileAtStartup(FileLocation.ofClasspath("/tsql-sonarlint.xml"))
    .restoreProfileAtStartup(FileLocation.ofClasspath("/apex-sonarlint.xml"))
    .build();

  private static WsClient adminWsClient;

  @TempDir
  private static Path sonarUserHome;

  private static SonarLintRpcServer backend;
  private static SonarLintRpcClientDelegate client;

  private static final Map<String, Boolean> analysisReadinessByConfigScopeId = new ConcurrentHashMap<>();

  @AfterAll
  static void stopBackend() throws ExecutionException, InterruptedException {
    backend.shutdown().get();
  }

  private static String singlePointOfExitRuleKey;

  @BeforeAll
  static void prepare() {
    adminWsClient = newAdminWsClient(ORCHESTRATOR);
    adminWsClient.settings().set(new SetRequest().setKey("sonar.forceAuthentication").setValue("true"));
    //ACR-c179ab5a642c416a86ac446cb4028eff
    adminWsClient.settings().set(new SetRequest().setKey(SONAR_LEGACY_SCA_FEATURE_ENABLED_PROPERTY_KEY).setValue("true"));
    adminWsClient.settings().set(new SetRequest().setKey(SONAR_EARLY_ACCESS_MISRA_ENABLED_PROPERTY_KEY).setValue("true"));

    removeGroupPermission("anyone", "scan");

    adminWsClient.users().create(new CreateRequest().setLogin(SONARLINT_USER).setPassword(SONARLINT_PWD).setName("SonarLint"));

    provisionProject(ORCHESTRATOR, PROJECT_KEY_C, "Sample C");
    provisionProject(ORCHESTRATOR, PROJECT_KEY_COBOL, "Sample Cobol");
    provisionProject(ORCHESTRATOR, PROJECT_KEY_TSQL, "Sample TSQL");
    provisionProject(ORCHESTRATOR, PROJECT_KEY_APEX, "Sample APEX");
    ORCHESTRATOR.getServer().associateProjectToQualityProfile(PROJECT_KEY_C, "c", "SonarLint IT C");
    ORCHESTRATOR.getServer().associateProjectToQualityProfile(PROJECT_KEY_COBOL, "cobol", "SonarLint IT Cobol");
    ORCHESTRATOR.getServer().associateProjectToQualityProfile(PROJECT_KEY_TSQL, "tsql", "SonarLint IT TSQL");
    ORCHESTRATOR.getServer().associateProjectToQualityProfile(PROJECT_KEY_APEX, "apex", "SonarLint IT APEX");
    if (ORCHESTRATOR.getServer().version().isGreaterThanOrEquals(10, 4)) {
      ORCHESTRATOR.getServer().restoreProfile(FileLocation.ofClasspath("/custom-secrets-sonarlint.xml"));
      provisionProject(ORCHESTRATOR, PROJECT_KEY_CUSTOM_SECRETS, "Sample Custom Secrets");
      ORCHESTRATOR.getServer().associateProjectToQualityProfile(PROJECT_KEY_CUSTOM_SECRETS, "secrets", "SonarLint IT Custom Secrets");
    }
    if (ORCHESTRATOR.getServer().version().isGreaterThanOrEquals(10, 5)) {
      provisionProject(ORCHESTRATOR, PROJECT_KEY_JCL, "Sample JCL");
      ORCHESTRATOR.getServer().associateProjectToQualityProfile(PROJECT_KEY_JCL, "jcl", "SonarLint IT JCL");
    }

    if (ORCHESTRATOR.getServer().version().isGreaterThanOrEquals(9, 4)) {
      singlePointOfExitRuleKey = "c:S1005";
    } else {
      singlePointOfExitRuleKey = "c:FunctionSinglePointOfExit";
    }
    if (ORCHESTRATOR.getServer().version().isGreaterThanOrEquals(2025, 4)) {
      ORCHESTRATOR.getServer().restoreProfile(FileLocation.ofClasspath("/cpp-misra-sonarlint.xml"));
      provisionProject(ORCHESTRATOR, PROJECT_KEY_MISRA, "Sample MISRA");
      ORCHESTRATOR.getServer().associateProjectToQualityProfile(PROJECT_KEY_MISRA, "cpp", "SonarLint IT MISRA");
    }
  }

  @AfterEach
  void stop() {
    analysisReadinessByConfigScopeId.forEach((scopeId, readiness) -> backend.getConfigurationService().didRemoveConfigurationScope(new DidRemoveConfigurationScopeParams(scopeId)));
    analysisReadinessByConfigScopeId.clear();
    rpcClientLogs.clear();
    ((MockSonarLintRpcClientDelegate) client).clear();
  }

  @Nested
  //ACR-8748fab7f10e429b815898f82901edb2
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class CommercialAnalyzers {

    @BeforeAll
    void prepare() throws IOException {
      startBackend(Map.of());
    }

    void start(String configScopeId, String projectKey) {
      bindProject(configScopeId, "project-" + projectKey, projectKey);
    }

    @AfterEach
    void stop() {
      ((MockSonarLintRpcClientDelegate) client).getRaisedIssues().clear();
    }

    @Test
    void analysisC_old_build_wrapper_prop(@TempDir File buildWrapperOutput) throws Exception {
      String configScopeId = "analysisC_old_build_wrapper_prop";
      start(configScopeId, PROJECT_KEY_C);

      var buildWrapperContent = "{\"version\":0,\"captures\":[" +
        "{" +
        "\"compiler\": \"clang\"," +
        "\"executable\": \"compiler\"," +
        "\"stdout\": \"#define __STDC_VERSION__ 201112L\n\"," +
        "\"stderr\": \"\"" +
        "}," +
        "{" +
        "\"compiler\": \"clang\"," +
        "\"executable\": \"compiler\"," +
        "\"stdout\": \"#define __cplusplus 201703L\n\"," +
        "\"stderr\": \"\"" +
        "}," +
        "{\"compiler\":\"clang\",\"cwd\":\"" +
        Paths.get("projects/" + PROJECT_KEY_C).toAbsolutePath().toString().replace("\\", "\\\\") +
        "\",\"executable\":\"compiler\",\"cmd\":[\"cc\",\"src/file.c\"]}]}";

      FileUtils.write(new File(buildWrapperOutput, "build-wrapper-dump.json"), buildWrapperContent, StandardCharsets.UTF_8);

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_C, "src/file.c", "sonar.cfamily.build-wrapper-output", buildWrapperOutput.getAbsolutePath());

      assertThat(rawIssues)
        .extracting(RaisedIssueDto::getRuleKey)
        .containsOnly("c:S3805", singlePointOfExitRuleKey);
    }

    @Test
    //ACR-5f1369766256444581151620c8a1045d
    @OnlyOnSonarQube(from = "8.8")
    void analysisC_new_prop() {
      String configScopeId = "analysisC_old_build_wrapper_prop";
      start(configScopeId, PROJECT_KEY_C);

      var buildWrapperContent = "{\"version\":0,\"captures\":[" +
        "{" +
        "\"compiler\": \"clang\"," +
        "\"executable\": \"compiler\"," +
        "\"stdout\": \"#define __STDC_VERSION__ 201112L\n\"," +
        "\"stderr\": \"\"" +
        "}," +
        "{" +
        "\"compiler\": \"clang\"," +
        "\"executable\": \"compiler\"," +
        "\"stdout\": \"#define __cplusplus 201703L\n\"," +
        "\"stderr\": \"\"" +
        "}," +
        "{\"compiler\":\"clang\",\"cwd\":\"" +
        Paths.get("projects/" + PROJECT_KEY_C).toAbsolutePath().toString().replace("\\", "\\\\") +
        "\",\"executable\":\"compiler\",\"cmd\":[\"cc\",\"src/file.c\"]}]}";

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_C, "src/file.c", "sonar.cfamily.build-wrapper-content", buildWrapperContent);

      assertThat(rawIssues)
        .extracting(RaisedIssueDto::getRuleKey)
        .containsOnly("c:S3805", singlePointOfExitRuleKey);
    }

    @Test
    //ACR-8a0f438b3add46f58be82a7ac7a7be6d
    @DisabledOnOs(OS.WINDOWS)
    @OnlyOnSonarQube(from = "2025.4")
    void analysisMisraRules(@TempDir Path tmpDir) throws IOException {
      //ACR-1411e342aa5f4446944d2845427f85ff
      assumeTrue(ORCHESTRATOR.getServer().version().compareTo(Version.create("2025.6")) < 0);
      var configScopeId = "analysisMisraRules";
      start(configScopeId, PROJECT_KEY_MISRA);
      var projectDir = Path.of("projects").resolve(PROJECT_KEY_MISRA);
      var filePath = projectDir.resolve("foo.cpp").toAbsolutePath().toString();
      var compileCommandsFilePath = tmpDir.resolve("compile_commands.json");
      Files.writeString(compileCommandsFilePath, """
        [
        {
          "directory": "%s",
          "command": "/usr/bin/c++ -g -std=gnu++20 -fdiagnostics-color=always -o CMakeFiles/untitled.dir/foo.cpp.o -c %s",
          "file": "%s",
          "output": "CMakeFiles/untitled.dir/foo.cpp.o"
        }
        ]""".formatted(projectDir.toAbsolutePath().toString(), filePath, filePath));

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_MISRA, "foo.cpp", "sonar.cfamily.compile-commands", compileCommandsFilePath.toAbsolutePath().toString());

      assertThat(rawIssues)
        .extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getPrimaryMessage)
        .containsOnly(tuple("cpp:M23_151", "Either add a parameter list or the \"&\" operator to this use of \"f\"."));
    }

    @Test
    void analysisCobol() {
      String configScopeId = "analysisCobol";
      start(configScopeId, PROJECT_KEY_COBOL);

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_COBOL, "src/Custmnt2.cbl", "sonar.cobol.file.suffixes", "cbl");

      assertThat(rawIssues).hasSize(1);
    }

    @Test
    @OnlyOnSonarQube(from = "10.5")
    void analysisJCL() {
      String configScopeId = "analysisJCL";
      start(configScopeId, PROJECT_KEY_JCL);

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_JCL, "GAM0VCDB.jcl");

      assertThat(rawIssues).hasSize(6);
    }

    @Test
    void analysisTsql() {
      String configScopeId = "analysisTsql";
      start(configScopeId, PROJECT_KEY_TSQL);

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_TSQL, "src/file.tsql");

      assertThat(rawIssues).hasSize(1);
    }

    @Test
    void analysisApex() {
      String configScopeId = "analysisApex";
      start(configScopeId, PROJECT_KEY_APEX);

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_APEX, "src/file.cls");

      assertThat(rawIssues).hasSize(1);
    }

    @Test
    @OnlyOnSonarQube(from = "10.4")
    void analysisCustomSecrets() {
      var configScopeId = "analysisCustomSecrets";
      start(configScopeId, PROJECT_KEY_CUSTOM_SECRETS);

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_CUSTOM_SECRETS, "src/file.md");

      assertThat(rawIssues)
        .extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getPrimaryMessage)
        .containsOnly(tuple("secrets:custom_secret_rule", "User-specified secrets should not be disclosed."));
    }
  }

  @Nested
  //ACR-e1976a8ec839403eb52a49cf3f82b001
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class WithEmbeddedAnalyzer {

    @BeforeAll
    void setup() throws IOException {
      startBackend(Map.of("cpp", PluginLocator.getCppPluginPath()));
    }

    /*ACR-c8f623c88b534e69973be822f48a8ec2
ACR-8c45e4275ebc4de99ead1108f17d0e1c
ACR-d649c0d7c7314b8d832993978076e835
ACR-cbd9d4973dce4b43a18e7ce243c624d0
     */
    @Test
    void analysisWithDeprecatedRuleKey() {
      var configScopeId = "analysisWithDeprecatedRuleKey";
      bindProject(configScopeId, "project-" + PROJECT_KEY_C, PROJECT_KEY_C);
      var buildWrapperContent = "{\"version\":0,\"captures\":[" +
        "{" +
        "\"compiler\": \"clang\"," +
        "\"executable\": \"compiler\"," +
        "\"stdout\": \"#define __STDC_VERSION__ 201112L\n\"," +
        "\"stderr\": \"\"" +
        "}," +
        "{" +
        "\"compiler\": \"clang\"," +
        "\"executable\": \"compiler\"," +
        "\"stdout\": \"#define __cplusplus 201703L\n\"," +
        "\"stderr\": \"\"" +
        "}," +
        "{\"compiler\":\"clang\",\"cwd\":\"" +
        Paths.get("projects/" + PROJECT_KEY_C).toAbsolutePath().toString().replace("\\", "\\\\") +
        "\",\"executable\":\"compiler\",\"cmd\":[\"cc\",\"src/file.c\"]}]}";

      var rawIssues = analyzeFile(configScopeId, PROJECT_KEY_C, "src/file.c", "sonar.cfamily.build-wrapper-content", buildWrapperContent);

      assertThat(rawIssues)
        .extracting(RaisedIssueDto::getRuleKey)
        .containsOnly("c:S3805", "c:S1005");
    }
  }

  @Nested
  //ACR-3f58d466c98545739d73c5d0e3768305
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @OnlyOnSonarQube(from = "2025.4")
  class Sca {
    @BeforeAll
    void prepare() throws IOException {
      startBackend(Map.of());
      provisionProject(ORCHESTRATOR, PROJECT_KEY_SCA, "Sample SCA");
    }

    @Test
    void sca_feature_should_be_enabled() {
      var configScopeId = "sca_check_enabled";
      analyzeMavenProject(ORCHESTRATOR, "sample-sca", Map.of("sonar.projectKey", PROJECT_KEY_SCA));
      bindProject(configScopeId, PROJECT_KEY_SCA, PROJECT_KEY_SCA);

      var supportedResponse = backend.getDependencyRiskService().checkSupported(new CheckDependencyRiskSupportedParams(configScopeId)).join();

      assertThat(supportedResponse.isSupported()).isTrue();
      assertThat(supportedResponse.getReason()).isNull();
    }
  }

  private static void bindProject(String configScopeId, String projectName, String projectKey) {
    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(
      List.of(new ConfigurationScopeDto(configScopeId, null, true, projectName,
        new BindingConfigurationDto(CONNECTION_ID, projectKey, true)))));
    await().atMost(30, SECONDS).untilAsserted(() -> assertThat(analysisReadinessByConfigScopeId).containsEntry(configScopeId, true));
  }

  private List<RaisedIssueDto> analyzeFile(String configScopeId, String projectDir, String filePathStr, String... properties) {
    var filePath = Path.of("projects").resolve(projectDir).resolve(filePathStr);
    var fileUri = filePath.toUri();
    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(
      List.of(new ClientFileDto(fileUri, Path.of(filePathStr), configScopeId, false, null, filePath.toAbsolutePath(), null, null, true)),
      List.of(),
      List.of()));

    var analyzeResponse = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(configScopeId, UUID.randomUUID(), List.of(fileUri), toMap(properties), true, System.currentTimeMillis())).join();

    assertThat(analyzeResponse.getFailedAnalysisFiles()).isEmpty();
    var raisedIssues = ((MockSonarLintRpcClientDelegate) client).getRaisedIssues(configScopeId);
    ((MockSonarLintRpcClientDelegate) client).getRaisedIssues().clear();
    return raisedIssues != null ? raisedIssues.values().stream().flatMap(List::stream).toList() : List.of();
  }

  private static void removeGroupPermission(String groupName, String permission) {
    adminWsClient.permissions().removeGroup(new RemoveGroupRequest()
      .setGroupName(groupName)
      .setPermission(permission));
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

  static void startBackend(Map<String, Path> connectedModeEmbeddedPluginPathsByKey) throws IOException {
    var clientToServerOutputStream = new PipedOutputStream();
    var clientToServerInputStream = new PipedInputStream(clientToServerOutputStream);

    var serverToClientOutputStream = new PipedOutputStream();
    var serverToClientInputStream = new PipedInputStream(serverToClientOutputStream);

    new BackendJsonRpcLauncher(clientToServerInputStream, serverToClientOutputStream);
    client = newDummySonarLintClient();
    var clientLauncher = new ClientJsonRpcLauncher(serverToClientInputStream, clientToServerOutputStream, client);

    backend = clientLauncher.getServerProxy();
    try {
      var languages = Set.of(JAVA, COBOL, C, CPP, TSQL, APEX, SECRETS, JCL);
      backend.initialize(
        new InitializeParams(IT_CLIENT_INFO, IT_TELEMETRY_ATTRIBUTES, HttpConfigurationDto.defaultConfig(), null,
          Set.of(BackendCapability.FULL_SYNCHRONIZATION, BackendCapability.PROJECT_SYNCHRONIZATION, BackendCapability.SECURITY_HOTSPOTS),
          sonarUserHome.resolve("storage"),
          sonarUserHome.resolve("work"),
          emptySet(),
          connectedModeEmbeddedPluginPathsByKey, languages, emptySet(), emptySet(),
          List.of(new SonarQubeConnectionConfigurationDto(CONNECTION_ID, ORCHESTRATOR.getServer().getUrl(), true)), emptyList(),
          sonarUserHome.toString(),
          Map.of(), false, null, false, null))
        .get();
    } catch (Exception e) {
      throw new IllegalStateException("Cannot initialize the backend", e);
    }
  }
}
