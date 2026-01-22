/*
ACR-40992e2b93074444a3066ba443034ab8
ACR-60638f07fa344736a31a6f65978dd79a
ACR-df009c72944841528788efe7d621688b
ACR-48f5db5fd64641a7a67b0d67952c1699
ACR-1e981c806e5e4a78ab41f1e35bf0e047
ACR-624eadc0b8b44677bbd2e1c0e964ab1e
ACR-84f0d9be7adc4e7ea35366749a9ed6b7
ACR-e9d0c19a7dc54cb19727f665f71cad1c
ACR-b1e739eed0e94dcc9efaaafcdf4060f0
ACR-3531130855954531a3e3ed55b76e8d35
ACR-e9234e76b8cb4571ae7393ff7c08a636
ACR-7e27c1e2161949da80a430036b0df638
ACR-878ad2d94f8843709818c265a7c18edd
ACR-d1fcd815f3c14aa9a52d458752ffc81c
ACR-741e0705b3524cffacb29d00644108b6
ACR-292c9a96de034032a82b23c3f1f1c61b
ACR-b30e7b5b451a4db8ac2fd94eb3f8e3d1
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
