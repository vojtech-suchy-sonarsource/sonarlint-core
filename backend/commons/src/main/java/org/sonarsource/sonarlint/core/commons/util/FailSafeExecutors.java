/*
ACR-e9d55d7260ec4fbd9abddddd6db45f82
ACR-1dbb756515614cecb911d16e56683f48
ACR-4d2a3be7a6c040a588a22b3f8ee3c6f5
ACR-42d2b29d4d1a4498b1baddd66c3705a5
ACR-7e6e2e684a3f48519bcd2479856c0ab1
ACR-1e851e4af4f4449284d8842398482420
ACR-b6606047bbc945be892a32db3c71cf2f
ACR-c06a3f983dd84ddaa326cdbccb0be73c
ACR-4cddb739dab54416a16cac6a995d3d71
ACR-c1630b468db24c159a6f932f7b22827f
ACR-1d2851adf6a14b96b621cf214b8ccb1c
ACR-e577aeefeb5d4f7bb94116eea540a915
ACR-c1ccdfff7ebc4a339d2ba6f3714aaabe
ACR-7c51cb55486e4a758a772f8951b756d0
ACR-239a0f20704b43989dca0c68f3a0f907
ACR-90e8cd693a744b298b9f07ec6f0ce423
ACR-3da33bcaf3ea4a4f9bd06b26d612670d
 */
package org.sonarsource.sonarlint.core.commons.util;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-ed7a5babda5e4eb4a12cd9aef1cda401
ACR-cdcd0eee15bd4591be2381d97dca15a3
 */
public class FailSafeExecutors {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private FailSafeExecutors() {
    //ACR-4071786833534e139e8f05d44d123641
  }

  public static ExecutorService newSingleThreadExecutor(String threadName) {
    return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, threadName)) {
      @Override
      protected void afterExecute(Runnable task, @Nullable Throwable throwable) {
        var extractedThrowable = extractThrowable(task, throwable);
        if (extractedThrowable != null) {
          LOG.error("An error occurred while executing a task in " + threadName, extractedThrowable);
        }
        super.afterExecute(task, throwable);
      }
    };
  }

  public static ScheduledExecutorService newSingleThreadScheduledExecutor(String threadName) {
    return new ScheduledThreadPoolExecutor(1, r -> new Thread(r, threadName)) {
      @Override
      protected void afterExecute(Runnable task, @Nullable Throwable throwable) {
        var extractedThrowable = extractThrowable(task, throwable);
        if (extractedThrowable != null) {
          LOG.error("An error occurred while executing a scheduled task in " + threadName, extractedThrowable);
        }
        super.afterExecute(task, throwable);
      }
    };
  }

  public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory) {
      @Override
      protected void afterExecute(Runnable task, @Nullable Throwable throwable) {
        var extractedThrowable = extractThrowable(task, throwable);
        if (extractedThrowable != null) {
          LOG.error("An error occurred while executing a task in " + Thread.currentThread(), extractedThrowable);
        }
        super.afterExecute(task, throwable);
      }
    };
  }

  @CheckForNull
  private static Throwable extractThrowable(Runnable task, @Nullable Throwable throwable) {
    if (throwable != null) {
      return throwable;
    }
    if (task instanceof FutureTask<?> futureTask) {
      try {
        if (futureTask.isDone() && !futureTask.isCancelled()) {
          futureTask.get();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        return e.getCause();
      } catch (CancellationException e) {
        //ACR-8ca49b2daa3043de9fecb56439cb73d9
      }
    }
    return null;
  }
}
