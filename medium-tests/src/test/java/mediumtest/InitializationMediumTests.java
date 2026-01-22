/*
ACR-d01f9b970eb84209829415ba730d5050
ACR-5d2dd3a4466941db8144b5b14e58fa22
ACR-9912056a31014ffaa51619dcf6013f35
ACR-722f08e9eed142cfaf5f003725d828c0
ACR-395c1365d5bd48739e6d5baa2a02c4e1
ACR-3d1a5babdbe442f7a6ac91de6a0c999f
ACR-a9e1004042a041218c207cc682011d59
ACR-1eb8395c136b4ef28d3e35bc25860396
ACR-e29e9095ff8b4bac8f520cdfafe65658
ACR-71f457ddf40c4d0dbbba8a5dbc0dc821
ACR-f331194ace7d413995632f05e7b50ab3
ACR-1436a617b2f647a986e4c68b0b71334f
ACR-17cd5b0fc38c4d11b8b9ec6d305c584b
ACR-eb3a87de81874ab6b219e7eff918c4c0
ACR-d1e66b6ed7024b8cb6b425386e5e855c
ACR-ddd283567a2d41e39983995f155af6b0
ACR-5622d2c0e1fb4dcbbc45508b133f093c
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
