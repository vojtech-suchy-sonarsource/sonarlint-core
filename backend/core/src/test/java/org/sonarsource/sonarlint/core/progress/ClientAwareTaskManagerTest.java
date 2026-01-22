/*
ACR-9efebd1e89554e75bda05075a3ac66e7
ACR-89deff5e173742769216cf2a763d8c42
ACR-48dc6c7b0ca24bc29fb3cf40a252f640
ACR-a6ed5f1a7cf542d181e47fae63277127
ACR-2e73c248e28a4d618abd5b54baf2ba8d
ACR-0bfb823749d54fabb336fed1f5b30242
ACR-47be746f7e684c5486759a620edfd159
ACR-3433f55ad4b14c7486aa63b5fab70c11
ACR-b3d3fc572ec54ca89f016303525971ec
ACR-bbf7a5575a5244d6b70e81463790bf17
ACR-033cfbd854ff471ea7097868a0bcad03
ACR-3c93250ebf254f5c89504f0026fc1e69
ACR-15eaeea7d4a54a8b8c41e54f3a06fe1b
ACR-dce018cde965434ea5f751b9d83dd3d1
ACR-bce719d21913468896026b1b9037b1d9
ACR-a0d1b431f53841c1b7b43ae2b5cb6a25
ACR-d0546ace02b9436c86ad38bb19e834a4
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
