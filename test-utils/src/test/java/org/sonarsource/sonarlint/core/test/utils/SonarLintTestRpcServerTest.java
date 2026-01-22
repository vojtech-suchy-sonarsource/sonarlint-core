/*
ACR-9304c7973f8846b8a5ac736061673133
ACR-f4c3581486154476b9a705331c96e193
ACR-74216cf9193f43c39965ae85784f91ad
ACR-4b26fc26125b4874afc3c0453e3dc6c8
ACR-3c543ea26acf4bbfb82e80766191ca9f
ACR-35ee33b1101644f2b0a88bc5e87b8db6
ACR-746526a042a24decbc5ec5e1fc52522c
ACR-040afba076d244f5bfef2e3e2a296b9b
ACR-92009a239aa541339e0aba4bd3df54b6
ACR-ce6b6099677340eb88dc94db01919f36
ACR-e4d6629b926544cdb44891c165ce9966
ACR-86dc63b5f9dd4ac286e1c44c5617fb02
ACR-2b23d86ad63e4b61a2d688aa44900711
ACR-110159f2be394f689415b485cce43218
ACR-0734e402df48478f8cd45043910446ff
ACR-7673d993148248188f0f46a98164b3d9
ACR-bf4b29e8bac04a869e18e06b12450a80
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
