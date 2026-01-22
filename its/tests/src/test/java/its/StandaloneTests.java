/*
ACR-03d71dc7026c49c5b40d637c71c92002
ACR-ed651a3f40914be987b0667aed495682
ACR-c071fe32230842f1afb35728f7d5a720
ACR-39245f3301f44cb1b6e940568cddefbb
ACR-b922e8fac52c4f71b0698be517bfc92f
ACR-6184eecd64c545f8b6de72b8a0d26d55
ACR-2ee3d7dcda92446ab7dcb400859f7074
ACR-ee23ded1e67f4130b7f19b5c9bb163bd
ACR-ca52930ccca5410d8c2a1e1120176b23
ACR-642dea89be5a47b7a5075e3f9930f558
ACR-d42432e65a2a4914ad140959348d40f8
ACR-ac55d9389b8e493db85a5d35479a1b66
ACR-4a992187476f4c38869fc7717541e5dd
ACR-a418b09e94504fc187a68639f98165c4
ACR-f3eaa936a8f641fcbf73bfb389f0e822
ACR-c7bf47ca01824d38a74dacd51e519654
ACR-935fab6ba61d42d0bb6fd43d00e1d7f5
 */
package its;

import its.utils.TestClientInputFile;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.rpc.client.ClientJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.impl.BackendJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.ClientConstantInfoDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleDefinitionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleParamDefinitionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleParamType;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.UpdateStandaloneRulesConfigurationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;

import static its.AbstractConnectedTests.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.COBOL;

class StandaloneTests {
  public static final TelemetryClientConstantAttributesDto IT_TELEMETRY_ATTRIBUTES = new TelemetryClientConstantAttributesDto("SonarLint ITs", "SonarLint ITs",
    "1.2.3", "4.5.6", Collections.emptyMap());
  public static final ClientConstantInfoDto IT_CLIENT_INFO = new ClientConstantInfoDto("clientName", "integrationTests");
  private static final String CONFIG_SCOPE_ID = "my-ide-project-name";
  @TempDir
  private File baseDir;
  @TempDir
  private static Path sonarUserHome;

  private static SonarLintRpcServer backend;
  private static SonarLintRpcClientDelegate client;

  @BeforeAll
  static void prepare() throws Exception {
    var clientToServerOutputStream = new PipedOutputStream();
    var clientToServerInputStream = new PipedInputStream(clientToServerOutputStream);

    var serverToClientOutputStream = new PipedOutputStream();
    var serverToClientInputStream = new PipedInputStream(serverToClientOutputStream);
    client = newDummySonarLintClient();
    new BackendJsonRpcLauncher(clientToServerInputStream, serverToClientOutputStream);
    var clientLauncher = new ClientJsonRpcLauncher(serverToClientInputStream, clientToServerOutputStream, client);
    backend = clientLauncher.getServerProxy();
    try {
      //ACR-eb68fc2daef14bd084212d051a75bafa
      var languages = Set.of(COBOL);
      System.out.println("Before backend initialize");
      backend.initialize(
        new InitializeParams(IT_CLIENT_INFO, IT_TELEMETRY_ATTRIBUTES, HttpConfigurationDto.defaultConfig(), null, Set.of(),
          sonarUserHome.resolve("storage"),
          sonarUserHome.resolve("work"),
          Set.of(Paths.get("../plugins/global-extension-plugin/target/global-extension-plugin.jar")), Collections.emptyMap(),
          languages, Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), sonarUserHome.toString(), Map.of(),
          false, null, false, null))
        .get();
      backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, false, CONFIG_SCOPE_ID, null))));
      System.out.println("After backend initialize");
    } catch (Exception e) {
      throw new IllegalStateException("Cannot initialize the backend", e);
    }
    Map<String, String> globalProps = new HashMap<>();
    globalProps.put("sonar.global.label", "It works");
  }

  @AfterEach
  void cleanup() {
    ((MockSonarLintRpcClientDelegate) client).clear();
  }

  @Test
  void checkRuleParameterDeclarations() throws ExecutionException, InterruptedException {
    var ruleDetails = backend.getRulesService().listAllStandaloneRulesDefinitions().get().getRulesByKey();
    assertThat(ruleDetails).hasSize(1);
    var incRule = ruleDetails.entrySet().iterator().next().getValue();
    assertThat(incRule.getParamsByKey()).hasSize(8);
    assertRuleHasParam(incRule, "stringParam", RuleParamType.STRING);
    assertRuleHasParam(incRule, "textParam", RuleParamType.TEXT);
    assertRuleHasParam(incRule, "boolParam", RuleParamType.BOOLEAN);
    assertRuleHasParam(incRule, "intParam", RuleParamType.INTEGER);
    assertRuleHasParam(incRule, "floatParam", RuleParamType.FLOAT);
    assertRuleHasParam(incRule, "enumParam", RuleParamType.STRING, "enum1", "enum2", "enum3");
    assertRuleHasParam(incRule, "enumListParam", RuleParamType.STRING, "list1", "list2", "list3");
    assertRuleHasParam(incRule, "multipleIntegersParam", RuleParamType.INTEGER, "80", "120", "160");
  }

  private static void assertRuleHasParam(RuleDefinitionDto rule, String paramKey, RuleParamType expectedType, String... possibleValues) {
    var param = rule.getParamsByKey().entrySet().stream().filter(p -> p.getKey().equals(paramKey)).findFirst();
    assertThat(param).isNotEmpty();
    assertThat(param.get().getValue())
      .extracting(RuleParamDefinitionDto::getType, RuleParamDefinitionDto::getPossibleValues)
      .containsExactly(expectedType, Arrays.asList(possibleValues));
  }

  @Test
  void globalExtension() throws Exception {
    prepareInputFile("foo.glob", "foo", false);

    var raisedIssues = analyzeFile(CONFIG_SCOPE_ID, baseDir.getAbsolutePath(), "foo.glob", "sonar.cobol.file.suffixes", "glob");
    assertThat(raisedIssues).extracting("ruleKey", "primaryMessage").containsOnly(
      tuple("global:inc", "Issue number 0"));

    backend.getRulesService().updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(
      Map.of("global:inc", new StandaloneRuleConfigDto(true, Map.of("stringParam", "polop", "textParam", "", "multipleIntegersParam", "80,160", "unknown", "parameter")))));

    raisedIssues = analyzeFile(CONFIG_SCOPE_ID, baseDir.getAbsolutePath(), "foo.glob", "sonar.cobol.file.suffixes", "glob");
    assertThat(raisedIssues).extracting("ruleKey", "primaryMessage").containsOnly(
      tuple("global:inc", "Issue number 1"));
  }

  private ClientInputFile prepareInputFile(String relativePath, String content, final boolean isTest, Charset encoding) throws IOException {
    final var file = new File(baseDir, relativePath);
    FileUtils.write(file, content, encoding);
    return new TestClientInputFile(baseDir.toPath(), file.toPath(), isTest, encoding);
  }

  private ClientInputFile prepareInputFile(String relativePath, String content, final boolean isTest) throws IOException {
    return prepareInputFile(relativePath, content, isTest, StandardCharsets.UTF_8);
  }

  private static SonarLintRpcClientDelegate newDummySonarLintClient() {
    return new MockSonarLintRpcClientDelegate() {
      @Override
      public void log(LogParams params) {
        System.out.println(params);
      }
    };
  }

  private List<RaisedIssueDto> analyzeFile(String configScopeId, String baseDir, String filePathStr, String... properties) {
    var filePath = Path.of("projects").resolve(baseDir).resolve(filePathStr);
    var fileUri = filePath.toUri();
    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(
      List.of(new ClientFileDto(fileUri, Path.of(filePathStr), configScopeId, false, null, filePath.toAbsolutePath(), null, null, true)),
      List.of(),
      List.of()
    ));

    var analyzeResponse = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(configScopeId, UUID.randomUUID(), List.of(fileUri), toMap(properties), true, System.currentTimeMillis())
    ).join();

    assertThat(analyzeResponse.getFailedAnalysisFiles()).isEmpty();
    //ACR-3d27a98044e9415b94828f07eba2b764
    await().atMost(Duration.ofMillis(200)).untilAsserted(() -> assertThat(((MockSonarLintRpcClientDelegate) client).getRaisedIssues(configScopeId)).isNotEmpty());
    var raisedIssues = ((MockSonarLintRpcClientDelegate) client).getRaisedIssues(configScopeId);
    ((MockSonarLintRpcClientDelegate) client).getRaisedIssues().clear();
    return raisedIssues != null ? raisedIssues.values().stream().flatMap(List::stream).toList() : List.of();
  }
}
