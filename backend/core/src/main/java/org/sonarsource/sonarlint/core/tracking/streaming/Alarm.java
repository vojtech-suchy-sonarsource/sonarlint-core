/*
ACR-7a72d385ec1f46d1999c12f38fce76d6
ACR-0a056aa61a08491196d47b576645c98e
ACR-13f06273a29f4e33a46f20aade9c54b1
ACR-55973625633b43a7a3fb2d66a9399cf6
ACR-131e846432544a569c86b046ccc26c76
ACR-b195415187384f9a9843dfb1c36ee01b
ACR-bd4d10df2eae481d93ea6912e2609ca4
ACR-f9c0be1d73e3471688052696fea70b09
ACR-e5e38863d61546d2976d6b8ef36138f3
ACR-b8cb51e1eaa340ccb8998c84cb1c3f13
ACR-9455f94b116040788ab8fb1c06aa0966
ACR-b25a62ae856c424293761fed50c2e7f9
ACR-c836c289fd16418b98d3d797347f2dfc
ACR-5df7717f8b0d491f8b7658e469a685f8
ACR-232bf8c42f7e454c9a4beb9e39bc1154
ACR-db91a40c286d416e9d4727cf2fb2dbfb
ACR-7b6ebecab04d4d7d8d103e19ab04d8b9
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
    //ACR-dff90430ee33453d9b2790c2f196148e
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
