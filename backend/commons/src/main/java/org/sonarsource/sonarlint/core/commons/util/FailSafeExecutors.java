/*
ACR-7b415c54cbfa4c828303179a9222ea47
ACR-53f55656fee941fe914369c02b591042
ACR-e9e4e83a7f0e4af4a7f59cfd0468601f
ACR-43f241739d4541eb888513b053c82e22
ACR-30530810071f4f4da30b01ff9483ed79
ACR-4ce84ba279cb450392e6c53ba3adcb61
ACR-40309f0fd51445c994771372b73f667e
ACR-16b7002811d7465695f96831130caca1
ACR-33f928731d9a4611b5544bcbeaa5d20a
ACR-e6ee46b6a09048bb87bddc863134380c
ACR-8fd1d36d28bc436593e92eb8908f8823
ACR-b7d45996d7884fe9a29dc08fb7e6a956
ACR-ac04f272e54946b297135e325bdf48ed
ACR-5cfd0364c7d540b89e3fc4e15c15fc98
ACR-69d61848e0564c91b567313363c7c327
ACR-1075fe4a8b8b4f2d8965154ae3595873
ACR-9bd03c1fcf1640fab1ab00f3e74d1207
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

/*ACR-7f4c7d04a06342669c07562113d663c9
ACR-3d0ae625ef414cb0ab0356cc72b51369
 */
public class FailSafeExecutors {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private FailSafeExecutors() {
    //ACR-7ff0e2e1084e4fbc8ab52db2d9b59f4f
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
        //ACR-3d6093d7349d4b12bb465e07ad62a21f
      }
    }
    return null;
  }
}
