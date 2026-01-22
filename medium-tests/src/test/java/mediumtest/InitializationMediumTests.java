/*
ACR-5773154a9d89499aabfff27048b92503
ACR-4ca926dfd8054e04a417745764a15333
ACR-cd5fddf0e42e481f972d89f33f968fc5
ACR-56ebeb127a0d47978277cdb2868cd7af
ACR-1ec2d6b4292d4ba5bf488b48f9e09f60
ACR-de22d9589902489ca7a9af37d906628f
ACR-8a4e854678d04aa5af8725f50c3b1974
ACR-5204a7543fd4456c9d16cd58868f6de4
ACR-67d135bae93c4fadacdd0e288e1769a4
ACR-d4bfc011d85e445e95b4e39426abbef3
ACR-a48760ba7ac640ff9d9227036f18d5fd
ACR-3ea4ae38644b40c5b7e567c50bec01e6
ACR-b4fce1a959794c7fa3f033730099af92
ACR-3cf4c2851df1445f903df3e3eb6ec2bc
ACR-aaf5e55801174414b33f7d532ac3eb6d
ACR-ab5a412efe3845d0bc9f5a2f925dd501
ACR-15a6ce9354ff4cd9bf9cd63513ff0ec4
 */
package mediumtest;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.ClientConstantInfoDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

class InitializationMediumTests {

  @SonarLintTest
  void it_should_fail_to_initialize_the_backend_twice(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .start();
    var telemetryInitDto = new TelemetryClientConstantAttributesDto("mediumTests", "mediumTests", "1.2.3", "4.5.6", emptyMap());
    var future = backend
      .initialize(new InitializeParams(new ClientConstantInfoDto("name", "productKey"), telemetryInitDto,
        HttpConfigurationDto.defaultConfig(), null, Set.of(),
        Path.of("unused"), Path.of("unused"),
        emptySet(), emptyMap(), emptySet(), emptySet(), emptySet(),
        emptyList(), emptyList(), "home", emptyMap(), false, null, false, null));

    assertThat(future)
      .failsWithin(Duration.ofSeconds(1))
      .withThrowableOfType(ExecutionException.class)
      .havingCause()
      .isInstanceOf(ResponseErrorException.class)
      .withMessage("Backend already initialized");
  }

}
