/*
ACR-a6ece55f21f4416f9a7bfc5d392c0bf8
ACR-acd62b0604964faf8b4ad61a0dc800d6
ACR-143d9fa90b4d4444b5700dda896ec7b5
ACR-937e0400229a4079a72f0fa2e96f2b06
ACR-12e377cbe31b45b98d233d95222effb7
ACR-6c1e23a1ab334ad7b48155f930b24955
ACR-81e61b2ac5bd4a6db8a2335ebebee714
ACR-ac13f217089a4470a41c254541b7f8a7
ACR-d13cd00a65e74896bfa4288c0407c201
ACR-b47c6914c1c84f79abe04a9941a45bc9
ACR-bc3e9ebd47e8403993dc132a77dafd77
ACR-b030a30b27f24dd7b464dddf22668b9c
ACR-240e428509a343f8b0c637b7e631426f
ACR-5e166027549047e9b92a448a46534c33
ACR-0307744ad22f4ba2b0195a9c58692af9
ACR-f777692bbd5540a18034af198684c8b2
ACR-7a1c1e460b5a4d99bfe5ebf4a2108c0b
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
