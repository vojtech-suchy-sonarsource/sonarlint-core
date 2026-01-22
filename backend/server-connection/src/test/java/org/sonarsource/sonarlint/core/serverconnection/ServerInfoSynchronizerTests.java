/*
ACR-0b6c9a4ab7284ffa8776cfd02e857ead
ACR-cebcf01f299e464ea378afe6abed21b4
ACR-f91e6732c45a4e00bbeeb9aede4a743b
ACR-84c63531471b4c3fa2c29484ee3f0328
ACR-8a06688d75c043fa970f543b10ad2d2c
ACR-9a9ff970bf4d4e748ec4056f9193774d
ACR-a6905b12eeee4baebf783fcdfe02d241
ACR-91a6d2440d0241ee8fc7c58d3d8b6eb5
ACR-3bdfd9baeda34e75aed68d986383dfb2
ACR-34b0f91af5494f1cb8e8f5d1df364bec
ACR-1da463016292427aac3404a9c8a5dbcf
ACR-bfd0184e800046edaca6a5d51ce55366
ACR-9b9986a28ae24863831f2b3565c87ece
ACR-39f7ac5782e44f8cbe5a91bcbfc5b13b
ACR-a86771f15bd642bb97b57cbe2f918a8e
ACR-5368480850b8479dadea2738b290166d
ACR-a8925af3c0144640b27b26288d8facb6
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
