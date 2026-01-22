/*
ACR-b999d007cdb34abba76b76ed3031a6fb
ACR-da16a55ec44d4f698d5638cf155b606e
ACR-fd262a71f0434ed3be8589ca5214e3af
ACR-5144d588634d4a908c1edf05e66e6d5d
ACR-196eb0efbef747598f66a7828d59b9ef
ACR-0ffbff523adb4dff98482bd3bf8b2ab3
ACR-4406a516539e4c9da224e7044e7c6a09
ACR-4e3dbd9f29ce4535b5854542aafa006a
ACR-9b9b6baba88a4532af46bd666347fc37
ACR-e717f8169bda4bcca6fe9619e6c767c2
ACR-156aae839ad7449eb9d6e4a19841bc4d
ACR-dcffd94b093342a480b699ee97812df4
ACR-075e1e74574044de904fb5e8270cb364
ACR-28e4cec251b14dbfb5d0a304ea969c7a
ACR-bf1e6f10cad44eaab881494e493c0029
ACR-86432385a215477c8b3375ec207acb75
ACR-f9d32c5ca35143c98ddeaf74499ec7eb
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
