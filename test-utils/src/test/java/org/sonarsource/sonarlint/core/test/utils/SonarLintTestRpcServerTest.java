/*
ACR-0155aa272dc54260bdaf26860f2c5b20
ACR-9ae1ddecbf7a43f8a4a9134e66e7a654
ACR-57652ca61c6c44d2bf78fc0dfd6bb8d6
ACR-c857313da53a49e9be23d36155d7d7c1
ACR-94f4f7865a094cfa9565fd7bbd84e7fc
ACR-537978ee6a4b449eb721bec8df4fffff
ACR-0b68f76c3ff94b61bf6bd264a4a87bec
ACR-19f626271f11429c845987cdcdc0fc99
ACR-4b7080f3f5484a7b8700c4b526364aeb
ACR-9d95152317a84e2a9b9cb8277cec79e5
ACR-6cb54d14bb2a448c84a981516a3a955b
ACR-5059329298ea4470a9f11720d92b67d1
ACR-297be458dbfe4d68916fec7e3d479453
ACR-a511cc060553469b892e9f5793c70775
ACR-d6b113f028da46db84b0d482677b0fdb
ACR-6f06f6cc21b847dd9603695cb114a155
ACR-89220348e2374067b745d28bd91539e7
 */
package org.sonarsource.sonarlint.core.test.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.client.ClientJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.ClientConstantInfoDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.SslConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SonarLintTestRpcServerTest {

  @Test
  void it_should_throw_an_assertion_exception_when_telemetry_file_does_not_exist(@TempDir Path userHome) throws IOException {
    var clientLauncher = mock(ClientJsonRpcLauncher.class);
    var rpcServer = mock(SonarLintRpcServer.class);
    when(rpcServer.initialize(any())).thenReturn(CompletableFuture.completedFuture(null));
    when(clientLauncher.getServerProxy()).thenReturn(rpcServer);
    var sonarLintTestRpcServer = new SonarLintTestRpcServer(mock(SonarLintRpcClientDelegate.class));
    sonarLintTestRpcServer
      .initialize(
        new InitializeParams(new ClientConstantInfoDto("", ""), new TelemetryClientConstantAttributesDto("product", null, null, null, null),
          new HttpConfigurationDto(new SslConfigurationDto(null, null, null, null, null, null), null, null, null, null), null, Set.of(), Paths.get(""), Paths.get(""), null, null,
          null, null, null, null, null, userHome.toString(), null, false, null, false, null))
      .join();

    var throwable = catchThrowable(sonarLintTestRpcServer::telemetryFileContent);

    assertThat(throwable).isInstanceOf(AssertionError.class);
  }

}
