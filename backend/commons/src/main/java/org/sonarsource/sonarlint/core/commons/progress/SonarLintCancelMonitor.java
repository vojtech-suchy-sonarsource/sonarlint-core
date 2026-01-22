/*
ACR-cefbb4e7406c4d7fa069774eb047c234
ACR-e881140aca064c2d8ff53ad646102cda
ACR-f644159f2d484169a0529d7196866e81
ACR-435a72096ad94aa794db050943bda8f6
ACR-5888a0642e8f472092b5cc6720386a40
ACR-66e0a54d6ac649118b33bf71d86167dc
ACR-e44506628e8440b083c8d201b4d02d41
ACR-b23bee2ecf3f4a86a7730322f6692f65
ACR-50e0dc7020764d2badd4cf5f99215e7b
ACR-a064d866da77464aa754487d386c33b3
ACR-3769e6dec6af416d808281683078651a
ACR-9829c0fae1154461b2299599697038f9
ACR-48d1bce3b4274a61ba33c5c8ce1e8974
ACR-613536d8fe0c4d43b0a31dde6459c759
ACR-2d4cd8925d6d47749505e7b90d918551
ACR-17a1aeb892434399b4ed3df01970ff8b
ACR-71db40712ef3409d970b21d3d0a4aadc
 */
package org.sonarsource.sonarlint.core.commons.progress;

import java.util.Deque;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SonarLintCancelMonitor {

  private boolean canceled;
  private final Deque<Runnable> downstreamCancelAction = new ConcurrentLinkedDeque<>();

  public synchronized void cancel() {
    canceled = true;
    downstreamCancelAction.forEach(Runnable::run);
    downstreamCancelAction.clear();
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void checkCanceled() {
    if (canceled) {
      throw new CancellationException();
    }
  }

  public synchronized void onCancel(Runnable action) {
    if (canceled) {
      action.run();
    } else {
      this.downstreamCancelAction.add(action);
    }
  }

  public void watchForShutdown(ExecutorServiceShutdownWatchable<?> executorService) {
    executorService.cancelOnShutdown(this);
  }
}
