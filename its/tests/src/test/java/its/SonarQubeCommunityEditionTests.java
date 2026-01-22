/*
ACR-1ac532d29ec64b10837efd84a5bc395c
ACR-03eab1e834d040f49a6668a1b77df0ed
ACR-e9c45af08d1d439fa29ecf198b3ca331
ACR-804d90d6e7634e99921f40b935b02199
ACR-bbad8a65f8874676bcaf8bf6a66c62b5
ACR-39e5a31ad27e4fe29b8228d5ecef9c8a
ACR-9381b694f5114ef2a67233aea57baa74
ACR-3a60411cc437484db0fb1ae5a57b8093
ACR-5d689095cbb949a0951dd8e7e2a80d36
ACR-082ae6760b3c474393805c0d421630f6
ACR-a9a6c2c342da4cbfbdc7f64c8882e419
ACR-706e1dfddf5443068372d983ea6d77c0
ACR-1552fcaec44a4c97b41c5a05ced2d480
ACR-5edef60a5f4d4f5a85107fdc5b25ffa4
ACR-50543a85b3bc448aa0bb20b755e5a67a
ACR-5168b7aff7074a7fbab1bde366617be4
ACR-dd615083a83b44d5a20f10a92a171463
 */
package its;

import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.locator.FileLocation;
import its.utils.OrchestratorUtils;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.users.CreateRequest;
import org.sonarsource.sonarlint.core.rpc.client.ClientJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.client.ConnectionNotFoundException;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.impl.BackendJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

import static java.util.Collections.emptySet;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;

class SonarQubeCommunityEditionTests extends AbstractConnectedTests {

  private static final String CONNECTION_ID = "orchestrator";

  @RegisterExtension
  static final OrchestratorExtension ORCHESTRATOR = OrchestratorUtils.defaultEnvBuilder()
    .addPlugin(FileLocation.of("../plugins/java-custom-rules/target/java-custom-rules-plugin.jar"))
    .setServerProperty("sonar.projectCreation.mainBranchName", MAIN_BRANCH_NAME)
    .build();

  @TempDir
  private static Path sonarUserHome;
  private static WsClient adminWsClient;
  private static SonarLintRpcServer backend;
  private static BackendJsonRpcLauncher serverLauncher;

  @BeforeAll
  static void startBackend() throws IOException {
    var clientToServerOutputStream = new PipedOutputStream();
    var clientToServerInputStream = new PipedInputStream(clientToServerOutputStream);

    var serverToClientOutputStream = new PipedOutputStream();
    var serverToClientInputStream = new PipedInputStream(serverToClientOutputStream);
    var client = newDummySonarLintClient();
    serverLauncher = new BackendJsonRpcLauncher(clientToServerInputStream, serverToClientOutputStream);
    var clientLauncher = new ClientJsonRpcLauncher(serverToClientInputStream, clientToServerOutputStream, client);

    backend = clientLauncher.getServerProxy();
    try {
      var enabledLanguages = Set.of(JAVA);
      backend.initialize(
        new InitializeParams(IT_CLIENT_INFO,
          IT_TELEMETRY_ATTRIBUTES, HttpConfigurationDto.defaultConfig(), null, Set.of(), sonarUserHome.resolve("storage"),
          sonarUserHome.resolve("work"),
          Collections.emptySet(), Collections.emptyMap(), enabledLanguages, emptySet(), emptySet(),
          List.of(new SonarQubeConnectionConfigurationDto(CONNECTION_ID, ORCHESTRATOR.getServer().getUrl(), true)),
          Collections.emptyList(),
          sonarUserHome.toString(),
          Map.of(), false, null, false, null))
        .get();
    } catch (Exception e) {
      throw new IllegalStateException("Cannot initialize the backend", e);
    }
  }

  @BeforeAll
  static void createSonarLintUser() {
    adminWsClient = newAdminWsClient(ORCHESTRATOR);
    adminWsClient.users().create(new CreateRequest().setLogin(SONARLINT_USER).setPassword(SONARLINT_PWD).setName("SonarLint"));
  }

  @AfterAll
  static void stopBackend() throws ExecutionException, InterruptedException {
    serverLauncher.getServer().shutdown().get();
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
      public void log(LogParams params) {
        rpcClientLogs.add(params);
      }

    };
  }
}
