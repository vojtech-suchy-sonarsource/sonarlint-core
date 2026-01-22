/*
ACR-6320d74c4a8847cfa0f82840af356f2b
ACR-29ae04255f2148ee8734165f4991f08f
ACR-89fb5ebf6889452091033f544b53e3ac
ACR-eac522c7e47d46dfb0b21e225a96b503
ACR-23e9886023924b30954043b5bdfbf5f5
ACR-9a4a6f26f01843d9979b4a3cbce4d773
ACR-7e642da12c0f41dab1051e200bf72513
ACR-d507e99ceae6446cb6170d2c40d68526
ACR-3ac6b810225140f7bec2513d27575cf7
ACR-2766bf247af04216a28273ab45cad8c0
ACR-3e0250c032054d6a8122bf57782c3ac2
ACR-b8f8b9529ff14957b078f27f3d257ba8
ACR-ae2d7ccf53154617bcf700e2347a7e79
ACR-703958e07640446e8ab6d38985337ae2
ACR-b0d9a8e8daba4c659d40a5d966aa5f18
ACR-4d65e80cbb094786a2120ce05a2dee66
ACR-da188c11ce7e46b99792e88ffe4146c3
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SonarLintRpcServerImplTests {

  @Test
  void it_should_fail_to_use_services_if_the_backend_is_not_initialized() {
    var in = new ByteArrayInputStream(new byte[0]);
    var out = new ByteArrayOutputStream();
    var backend = new SonarLintRpcServerImpl(in, out);

    assertThat(backend.getTelemetryService().getStatus())
      .failsWithin(1, TimeUnit.MINUTES)
      .withThrowableOfType(ExecutionException.class)
      .withCauseInstanceOf(IllegalStateException.class)
      .withStackTraceContaining("Backend is not initialized");
  }

  @Test
  void it_should_silently_shutdown_the_backend_if_it_was_not_initialized() {
    var in = new ByteArrayInputStream(new byte[0]);
    var out = new ByteArrayOutputStream();
    var backend = new SonarLintRpcServerImpl(in, out);

    var future = backend.shutdown();

    assertThat(future)
      .succeedsWithin(Duration.ofSeconds(1));
  }

}
