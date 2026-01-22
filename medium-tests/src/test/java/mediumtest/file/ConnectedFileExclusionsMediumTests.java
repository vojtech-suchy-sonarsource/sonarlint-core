/*
ACR-cd8923511aae4713b4ad36a4671a0824
ACR-7385f54fe33540c4b4fdbbad498dac77
ACR-b3ecf9dc0c6f443a915e6d3044336e2d
ACR-9b7aa64e200a45c3b6085db4717cbc7c
ACR-06b4a3b21c3845f6bbce2fe07fc35b3b
ACR-d84bc30720a847cfb74f70f510b914c2
ACR-f3dbb108005349ec968d3d20fa75db84
ACR-b6f6c53c9e9743308da29790cb7d3583
ACR-adbdb0b34dd040e1922fed0384af99c3
ACR-a0ed515d30034c99ae7387b0de348092
ACR-06ac703436c848fa8fba663dd87357e4
ACR-3e5434cd20ec47b0a0f91fad903a497e
ACR-19d50c3973fc42eab996bf8f7bc6687c
ACR-0aa337166f8040e2878be680043bcd54
ACR-e8a4234cb99d4906a1e4eb10fcb7112d
ACR-c96b5ecde89d474e96bef3f0a1403579
ACR-b11fad290fa141a8a6f7a0bf7bcaa103
 */
package mediumtest.file;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.FileStatusDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.GetFilesStatusParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Settings;
import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import org.sonarsource.sonarlint.core.test.utils.server.ServerFixture;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.PROJECT_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.test.utils.ProtobufUtils.protobufBody;

class ConnectedFileExclusionsMediumTests {

  private static final String MYSONAR = "mysonar";
  private static final String CONFIG_SCOPE_ID = "myProject1";
  private static final String PROJECT_KEY = "test-project-2";

  private String previousSyncPeriod;

  @BeforeEach
  void prepare() {
    previousSyncPeriod = System.getProperty("sonarlint.internal.synchronization.scope.period");
    System.setProperty("sonarlint.internal.synchronization.scope.period", "1");
  }

  @AfterEach
  void stop() {
    if (previousSyncPeriod != null) {
      System.setProperty("sonarlint.internal.synchronization.scope.period", previousSyncPeriod);
    } else {
      System.clearProperty("sonarlint.internal.synchronization.scope.period");
    }
  }

  @SonarLintTest
  void fileInclusionsExclusions(SonarLintTestHarness harness, @TempDir Path tmp) throws InterruptedException {
    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY)
      .start();

    var mainFile1 = tmp.resolve("foo.xoo");
    var mainFile1Dto = new ClientFileDto(mainFile1.toUri(), tmp.resolve(mainFile1), CONFIG_SCOPE_ID, false, StandardCharsets.UTF_8.name(), mainFile1, null, null, true);
    var mainFile2 = tmp.resolve("src/foo2.xoo");
    var mainFile2Dto = new ClientFileDto(mainFile2.toUri(), tmp.resolve(mainFile2), CONFIG_SCOPE_ID, false, StandardCharsets.UTF_8.name(), mainFile2, null, null, true);
    var testFile1 = tmp.resolve("fooTest.xoo");
    var testFile1Dto = new ClientFileDto(testFile1.toUri(), tmp.resolve(testFile1), CONFIG_SCOPE_ID, true, StandardCharsets.UTF_8.name(), testFile1, null, null, true);
    var testFile2 = tmp.resolve("test/foo2Test.xoo");
    var testFile2Dto = new ClientFileDto(testFile2.toUri(), tmp.resolve(testFile2), CONFIG_SCOPE_ID, true, StandardCharsets.UTF_8.name(), testFile2, null, null, true);

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(mainFile1Dto, mainFile2Dto, testFile1Dto, testFile2Dto))
      .build();

    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, server)
      .withBoundConfigScope(CONFIG_SCOPE_ID, MYSONAR, PROJECT_KEY)
      .withBackendCapability(FULL_SYNCHRONIZATION, PROJECT_SYNCHRONIZATION)
      .start(fakeClient);

    verify(fakeClient, timeout(5000).times(1)).didSynchronizeConfigurationScopes(Set.of(CONFIG_SCOPE_ID));

    var future1 = backend.getFileService()
      .getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of(mainFile1.toUri(), mainFile2.toUri(), testFile1.toUri(), testFile2.toUri()))));
    assertThat(future1).succeedsWithin(5, TimeUnit.SECONDS);
    assertThat(future1.join().getFileStatuses().entrySet())
      .extracting(Map.Entry::getKey, e -> e.getValue().isExcluded())
      .containsExactlyInAnyOrder(
        tuple(mainFile1.toUri(), false),
        tuple(mainFile2.toUri(), false),
        tuple(testFile1.toUri(), false),
        tuple(testFile2.toUri(), false));

    mockSonarProjectSettings(server, Map.of("sonar.inclusions", "src/**"));
    forceSyncOfConfigScope(backend, fakeClient);

    var future2 = backend.getFileService()
      .getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of(mainFile1.toUri(), mainFile2.toUri(), testFile1.toUri(), testFile2.toUri()))));
    await().untilAsserted(() -> assertThat(future2.join().getFileStatuses().entrySet())
      .extracting(Map.Entry::getKey, e -> e.getValue().isExcluded())
      .containsExactlyInAnyOrder(
        tuple(mainFile1.toUri(), true),
        tuple(mainFile2.toUri(), false),
        tuple(testFile1.toUri(), false),
        tuple(testFile2.toUri(), false)));

    mockSonarProjectSettings(server, Map.of("sonar.inclusions", "file:**/src/**"));
    forceSyncOfConfigScope(backend, fakeClient);

    var future3 = backend.getFileService()
      .getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of(mainFile1.toUri(), mainFile2.toUri(), testFile1.toUri(), testFile2.toUri()))));
    await().untilAsserted(() -> assertThat(future3.join().getFileStatuses().entrySet())
      .extracting(Map.Entry::getKey, e -> e.getValue().isExcluded())
      .containsExactlyInAnyOrder(
        tuple(mainFile1.toUri(), true),
        tuple(mainFile2.toUri(), false),
        tuple(testFile1.toUri(), false),
        tuple(testFile2.toUri(), false)));

    mockSonarProjectSettings(server, Map.of("sonar.exclusions", "src/**"));
    forceSyncOfConfigScope(backend, fakeClient);

    var future4 = backend.getFileService()
      .getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of(mainFile1.toUri(), mainFile2.toUri(), testFile1.toUri(), testFile2.toUri()))));
    await().untilAsserted(() -> assertThat(future4.join().getFileStatuses().entrySet())
      .extracting(Map.Entry::getKey, e -> e.getValue().isExcluded())
      .containsExactlyInAnyOrder(
        tuple(mainFile1.toUri(), false),
        tuple(mainFile2.toUri(), true),
        tuple(testFile1.toUri(), false),
        tuple(testFile2.toUri(), false)));

    mockSonarProjectSettings(server, Map.of("sonar.test.inclusions", "test/**"));
    forceSyncOfConfigScope(backend, fakeClient);

    var future5 = backend.getFileService()
      .getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of(mainFile1.toUri(), mainFile2.toUri(), testFile1.toUri(), testFile2.toUri()))));
    await().untilAsserted(() -> assertThat(future5.join().getFileStatuses().entrySet())
      .extracting(Map.Entry::getKey, e -> e.getValue().isExcluded())
      .containsExactlyInAnyOrder(
        tuple(mainFile1.toUri(), false),
        tuple(mainFile2.toUri(), false),
        tuple(testFile1.toUri(), true),
        tuple(testFile2.toUri(), false)));

    mockSonarProjectSettings(server, Map.of("sonar.test.exclusions", "test/**"));
    forceSyncOfConfigScope(backend, fakeClient);

    var future6 = backend.getFileService()
      .getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of(mainFile1.toUri(), mainFile2.toUri(), testFile1.toUri(), testFile2.toUri()))));
    await().untilAsserted(() -> assertThat(future6.join().getFileStatuses().entrySet())
      .extracting(Map.Entry::getKey, e -> e.getValue().isExcluded())
      .containsExactlyInAnyOrder(
        tuple(mainFile1.toUri(), false),
        tuple(mainFile2.toUri(), false),
        tuple(testFile1.toUri(), false),
        tuple(testFile2.toUri(), true)));

    mockSonarProjectSettings(server, Map.of("sonar.inclusions", "file:**/src/**", "sonar.test.exclusions", "**/*Test.*"));
    forceSyncOfConfigScope(backend, fakeClient);

    var future7 = backend.getFileService()
      .getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of(mainFile1.toUri(), mainFile2.toUri(), testFile1.toUri(), testFile2.toUri()))));
    await().untilAsserted(() -> assertThat(future7.join().getFileStatuses().entrySet())
      .extracting(Map.Entry::getKey, e -> e.getValue().isExcluded())
      .containsExactlyInAnyOrder(
        tuple(mainFile1.toUri(), true),
        tuple(mainFile2.toUri(), false),
        tuple(testFile1.toUri(), true),
        tuple(testFile2.toUri(), true)));
  }

  @SonarLintTest
  void it_should_not_try_to_compute_exclusions_when_storage_is_empty(SonarLintTestHarness harness, @TempDir Path tmp) {
    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY)
      .start();

    var mainFile1 = tmp.resolve("src/foo1.xoo");
    var mainFile1Dto = new ClientFileDto(mainFile1.toUri(), tmp.resolve(mainFile1), CONFIG_SCOPE_ID, false, StandardCharsets.UTF_8.name(), mainFile1, null, null, true);

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(mainFile1Dto))
      .build();

    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, server)
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withBackendCapability(FULL_SYNCHRONIZATION, PROJECT_SYNCHRONIZATION)
      .start(fakeClient);
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(CONFIG_SCOPE_ID, new BindingConfigurationDto(MYSONAR, PROJECT_KEY, true)));

    //ACR-333b96b0fbb54bf5bcecfe0b2296af94
    var response = backend.getFileService().getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of((mainFile1.toUri()))))).join();

    assertThat(response.getFileStatuses().values())
      .extracting(FileStatusDto::isExcluded)
      .containsOnly(false);
  }

  @SonarLintTest
  void it_should_fallback_to_default_charset_if_encoding_is_unknown(SonarLintTestHarness harness, @TempDir Path tmp) throws InterruptedException {
    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY)
      .start();

    var mainFile1 = tmp.resolve("src/foo1.xoo");
    var mainFile1Dto = new ClientFileDto(mainFile1.toUri(), tmp.resolve(mainFile1), CONFIG_SCOPE_ID, false, "wrongCharset", mainFile1, "Toto", null, true);

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(mainFile1Dto))
      .build();
    mockSonarProjectSettings(server, Map.of("sonar.exclusions", "src/**"));

    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, server)
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withBackendCapability(FULL_SYNCHRONIZATION, PROJECT_SYNCHRONIZATION)
      .start(fakeClient);
    mockSonarProjectSettings(server, Map.of("sonar.exclusions", "src/**"));
    forceSyncOfConfigScope(backend, fakeClient);

    var response = backend.getFileService().getFilesStatus(new GetFilesStatusParams(Map.of(CONFIG_SCOPE_ID, List.of((mainFile1.toUri()))))).join();

    assertThat(response.getFileStatuses().values())
      .extracting(FileStatusDto::isExcluded)
      .containsOnly(true);
  }

  private void forceSyncOfConfigScope(SonarLintTestRpcServer backend, SonarLintBackendFixture.FakeSonarLintRpcClient fakeClient) throws InterruptedException {
    Thread.sleep(100);
    Mockito.clearInvocations(fakeClient);
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(CONFIG_SCOPE_ID, new BindingConfigurationDto(null, null, true)));
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(CONFIG_SCOPE_ID, new BindingConfigurationDto(MYSONAR, PROJECT_KEY, true)));
    verify(fakeClient, timeout(5000).atLeastOnce()).didSynchronizeConfigurationScopes(Set.of(CONFIG_SCOPE_ID));
  }

  private void mockSonarProjectSettings(ServerFixture.Server server, Map<String, String> settings) {
    var reponseBuilder = Settings.ValuesWsResponse.newBuilder();
    settings.forEach((k, v) -> reponseBuilder.addSettings(Settings.Setting.newBuilder()
      .setKey(k)
      .setValue(v)));
    server.getMockServer().stubFor(get("/api/settings/values.protobuf?component=" + PROJECT_KEY)
      .willReturn(aResponse().withResponseBody(protobufBody(reponseBuilder.build()))));
  }

}
