/*
ACR-12eb56b63a1c4ac3a7f61e106a7949af
ACR-5f63e5d890fb4183baa499592deace18
ACR-cefce29d8f864b68a8d686419a18fabe
ACR-18f782317f5549109bf5f7f352ae6bea
ACR-2608b8f0e1944b8bbaff3ef2ab1c421b
ACR-cbdaceff16d6491da4dd56a0cb98587e
ACR-3ec3818899a74a49a89ea4bb29f396e0
ACR-ade655e5ea3c4b868bd97cbe4da7130c
ACR-fac5ac5e8e1f415fa949b5a674d7277b
ACR-24f49b0742af4f6e88751880e0ab8492
ACR-c987e0fe8ac94aa6bd25d24d0cebc50c
ACR-a8b1df54bf9848ef9ac6389b7e047477
ACR-108488e4b6b146a385fe67f2425181e8
ACR-b3b869cafcbf46a2bb28ab002269009e
ACR-7802212cd02e4de8ab1fc356d0d5c877
ACR-5f464337304f48c9a71e86dc9b5d3793
ACR-629fdc0ed6064960a3709fa5ace56310
 */
package org.sonarsource.sonarlint.core.tracking.streaming;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;

public class Alarm {
  private final Duration duration;
  private final Runnable endRunnable;
  private final ScheduledExecutorService executorService;
  private ScheduledFuture<?> scheduledFuture;

  public Alarm(String name, Duration duration, Runnable endRunnable) {
    this.duration = duration;
    this.endRunnable = endRunnable;
    this.executorService = FailSafeExecutors.newSingleThreadScheduledExecutor(name);
  }

  public void schedule() {
    //ACR-5419c0efab90454586ab7f3220b3d99d
    if (scheduledFuture == null) {
      scheduledFuture = executorService.schedule(this::notifyEnd, duration.toMillis(), TimeUnit.MILLISECONDS);
    }
  }

  public void reset() {
    cancelRunning();
    schedule();
  }

  private void notifyEnd() {
    if (!executorService.isShutdown()) {
      scheduledFuture = null;
      endRunnable.run();
    }
  }

  public void shutdownNow() {
    cancelRunning();
    executorService.shutdownNow();
  }

  private void cancelRunning() {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(false);
    }
    scheduledFuture = null;
  }
}
