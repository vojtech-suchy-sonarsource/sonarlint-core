/*
ACR-06248efa49fd4f0eb3751a9ed2068703
ACR-c2f6c10b0e264a01a345b189aaffc33a
ACR-656b2892b5b043ef99f756643aca22bc
ACR-849ccccc02a24415b3a3f4e93c437f1a
ACR-3722680c6a684896949afdc1cbc99377
ACR-87c2d3d2f01c41a9875a0d5ca0e3342f
ACR-141e17eeed684ae8a1d963fe0a5750a6
ACR-238887fe223a4dcdb3fb40e4b7aace22
ACR-2b64562b1db94faea95dbfbbbe501694
ACR-f40d16ae772b4d00bd123664f59d0cd1
ACR-f1a200f3b7174157860d5add63e0a09e
ACR-5c653ad8222e4bdb813d7f49434bf7c1
ACR-2a9403b801bb4b50a8475bae03623360
ACR-999db8454f4b4ad48897742b96b3d7b7
ACR-7c82fad5a0184587b6b0e8adf0cdde56
ACR-3ba855f03ecc4939ad2cb97d76520383
ACR-ffce67c4e855458eb9bbc6bebaf32020
 */
package org.sonarsource.sonarlint.core.progress;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.api.progress.CanceledException;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientAwareTaskManagerTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void it_should_throw_when_interrupted() throws InterruptedException {
    var client = mock(SonarLintRpcClient.class);
    when(client.startProgress(any())).thenReturn(new CompletableFuture<>());
    var taskManager = new ClientAwareTaskManager(client);
    var caughtException = new AtomicReference<Exception>();
    var thread = new Thread(() -> {
      try {
        taskManager.createAndRunTask("configScopeId", UUID.randomUUID(), "Title", null, true, true, progressIndicator -> {
        }, new SonarLintCancelMonitor());
      } catch (Exception e) {
        caughtException.set(e);
      }
    });
    thread.start();
    Thread.sleep(500);

    thread.interrupt();

    await().untilAsserted(() -> assertThat(caughtException.get()).isInstanceOf(CanceledException.class));
  }

}
