/*
ACR-a97ecde1844d4709b066cd71cae743fe
ACR-c38a158747a54a1e8c7318bf47225d66
ACR-3e5e3fc3130449138d9c2a801ebba971
ACR-2941618867c041ca8c5fc0d532a7595f
ACR-dda2d9e683db49e8b2b8ffce9fd12faa
ACR-382132a16880446b94a38ae279a2048f
ACR-4bdcceead9b04d47b94b6fea472308b0
ACR-4b6d0fec2ee84c0fb11b1cd743f303f8
ACR-bfdfaba11a8f47d4b3ac49ce16ebe129
ACR-6a2339325b1443c69ee616122b0e8d47
ACR-9065854fde374d588b6727900aab3d21
ACR-ee50f9b20cef426abe9c3e1f37b9ddd6
ACR-601564faeed444e2a0109cedd8ea2bde
ACR-70536051c24840d6ae5ff00ed9b00e9e
ACR-a4197b5b99bc4c7ab6dd069394982b7a
ACR-4868074eb6ad44de85a2ffd019cea08b
ACR-d85128f9f27c415481110482c8161c01
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import testutils.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UserSynchronizerTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  @TempDir
  Path tmpDir;
  private UserSynchronizer synchronizer;
  private ConnectionStorage storage;

  @BeforeEach
  void prepare() {
    var databaseService = mock(SonarLintDatabase.class);
    storage = new ConnectionStorage(tmpDir, "connectionId", databaseService);
    synchronizer = new UserSynchronizer(storage);
  }

  @Test
  void it_should_synchronize_user_id_on_sonarcloud() {
    mockServer.addStringResponse("/api/users/current", """
      {
        "isLoggedIn": true,
        "id": "16c9b3b3-3f7e-4d61-91fe-31d731456c08",
        "login": "obiwan.kenobi"
      }""");

    var serverApi = new ServerApi(mockServer.endpointParams("orgKey"), HttpClientProvider.forTesting().getHttpClient());
    synchronizer.synchronize(serverApi, new SonarLintCancelMonitor());

    var storedUserId = storage.user().read();
    assertThat(storedUserId)
      .isPresent()
      .contains("16c9b3b3-3f7e-4d61-91fe-31d731456c08");
  }

  @Test
  void it_should_synchronize_user_id_on_sonarqube_server() {
    mockServer.addStringResponse("/api/users/current", """
      {
        "isLoggedIn": true,
        "id": "00000000-0000-0000-0000-000000000001",
        "login": "obiwan.kenobi"
      }""");

    var serverApi = new ServerApi(mockServer.endpointParams(), HttpClientProvider.forTesting().getHttpClient());
    synchronizer.synchronize(serverApi, new SonarLintCancelMonitor());

    var storedUserId = storage.user().read();
    assertThat(storedUserId)
      .isPresent()
      .contains("00000000-0000-0000-0000-000000000001");
  }

  @Test
  void it_should_not_store_null_user_id() {
    mockServer.addStringResponse("/api/users/current", "{}");

    var serverApi = new ServerApi(mockServer.endpointParams("orgKey"), HttpClientProvider.forTesting().getHttpClient());
    synchronizer.synchronize(serverApi, new SonarLintCancelMonitor());

    var storedUserId = storage.user().read();
    assertThat(storedUserId).isEmpty();
  }

  @Test
  void it_should_store_user_id_in_correct_file() throws IOException {
    mockServer.addStringResponse("/api/users/current", """
      {
        "isLoggedIn": true,
        "id": "test-user-id",
        "login": "test.user"
      }""");

    var serverApi = new ServerApi(mockServer.endpointParams("orgKey"), HttpClientProvider.forTesting().getHttpClient());
    synchronizer.synchronize(serverApi, new SonarLintCancelMonitor());

    var connectionPath = tmpDir.resolve("636f6e6e656374696f6e4964");
    var userFile = connectionPath.resolve("user.pb");
    assertThat(userFile).exists();
    assertThat(Files.size(userFile)).isGreaterThan(0);
  }

}
