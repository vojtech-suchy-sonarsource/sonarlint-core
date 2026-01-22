/*
ACR-9efe613363394016a0c3cab01fb01e75
ACR-05dc6547c20d465a8aa586ec0f89579d
ACR-f81781068e3945f095c4302389c8eeb9
ACR-f6c6ba7e502d4587b19d6b01529e3db1
ACR-94b7c6f4700e4edd9ee16fbc396d60de
ACR-fe6d5656f01c4ecaa659e886e9a9f7b0
ACR-e30c38edbbb7453085b8955f68549b95
ACR-e1e29814a1f24c7f9c14fe3dd90168bc
ACR-5b6d000400f147aeb5de70549c0e5c38
ACR-d58445ac456d4b7aa4049c2198ed3c4b
ACR-1300f76f918e48388e113aaa6cefa925
ACR-d93f83b39efd46ada71d63c115fb2203
ACR-bc9b815200284cfca2e1bd378086807b
ACR-4f9c44125921486eab46c240efcd000c
ACR-f130b2b4977d441b8c3a5119a9973297
ACR-f73930a6fafe4da590ae157a2f2e8273
ACR-2d5efc07476a4f61a36597a924dd30e5
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Settings;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;
import testutils.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

class ServerInfoSynchronizerTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  @TempDir
  Path tmpDir;
  private ServerInfoSynchronizer synchronizer;

  @BeforeEach
  void prepare() {
    var databaseService = mock(SonarLintDatabase.class);
    var storage = new ConnectionStorage(tmpDir, "connectionId", databaseService);
    synchronizer = new ServerInfoSynchronizer(storage);
  }

  @Test
  void it_should_read_version_from_storage_when_available() throws IOException {
    var connectionPath = tmpDir.resolve("636f6e6e656374696f6e4964");
    Files.createDirectory(connectionPath);
    ProtobufFileUtil.writeToFile(Sonarlint.ServerInfo.newBuilder().setVersion("1.0.0").build(), connectionPath.resolve("server_info.pb"));

    var storedServerInfo = synchronizer.readOrSynchronizeServerInfo(new ServerApi(mockServer.endpointParams(), HttpClientProvider.forTesting().getHttpClient()),
      new SonarLintCancelMonitor());

    assertThat(storedServerInfo)
      .extracting(StoredServerInfo::version)
      .hasToString("1.0.0");
  }

  @Test
  void it_should_synchronize_version_and_settings() {
    mockServer.addStringResponse("/api/system/status", "{\"id\": \"20160308094653\",\"version\": \"9.9\",\"status\": \"UP\"}");
    mockServer.addStringResponse("/api/features/list", "[\"sca\"]");
    mockServer.addProtobufResponse("/api/settings/values.protobuf", Settings.ValuesWsResponse.newBuilder()
      .addSettings(Settings.Setting.newBuilder()
        .setKey("sonar.multi-quality-mode.enabled")
        .setValue("true"))
      .addSettings(Settings.Setting.newBuilder()
        .setKey("sonar.earlyAccess.misra.enabled")
        .setValue("true"))
      .build());

    var storedServerInfo = synchronizer.readOrSynchronizeServerInfo(new ServerApi(mockServer.endpointParams(), HttpClientProvider.forTesting().getHttpClient()),
      new SonarLintCancelMonitor());

    assertThat(storedServerInfo)
      .extracting(StoredServerInfo::version, StoredServerInfo::features, StoredServerInfo::globalSettings)
      .containsExactly(Version.create("9.9"), Set.of(Feature.SCA),
        new ServerSettings(Map.of(
          "sonar.multi-quality-mode.enabled", "true",
          "sonar.earlyAccess.misra.enabled", "true",
          "sonar.misracompliance.enabled", "true")));
  }

  @Test
  void it_should_fail_when_server_is_down() {
    mockServer.addStringResponse("/api/system/status", "{\"id\": \"20160308094653\",\"version\": \"9.9\",\"status\": \"DOWN\"}");

    var throwable = catchThrowable(
      () -> synchronizer.readOrSynchronizeServerInfo(new ServerApi(mockServer.endpointParams(), HttpClientProvider.forTesting().getHttpClient()), new SonarLintCancelMonitor()));

    assertThat(throwable)
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Server not ready (DOWN)");
  }
}
