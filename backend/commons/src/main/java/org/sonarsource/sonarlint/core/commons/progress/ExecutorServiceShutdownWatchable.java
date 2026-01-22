/*
ACR-84e85396f7254b4ca5e2db92770c7a3c
ACR-0659207eb2284e53aaecbca26cec94bf
ACR-9613b74e980b424babe578ab29464bf0
ACR-c0104e562f484c29b5924faf1cf7ef9d
ACR-602bca44a34c4d82b3edbaf354cd6dfc
ACR-cef4735760dc462684f4149c456295de
ACR-0d4c4dcb5bcf436093a29744004ffd80
ACR-ef64ec11f68e460196a24d1f599b20ce
ACR-6560c26d9bfb4295988d785a2301f7e5
ACR-47e0ce79f00d41b6b748260639dc117e
ACR-3d25df578aba4742bcf17eeeea054547
ACR-66bd1f2073304278b3dcf447738bcd1c
ACR-351b393f4d7f4f0ab075efc7a14c85d2
ACR-6a21e880984e491e974ae9efa152a333
ACR-0e7d75ca5f24464daec99e2d1c6bfed2
ACR-66e7944dd6c7405faeb7b4c39ef26546
ACR-402b5bbb6e7041719f72236db0cfb238
 */
package org.sonarsource.sonarlint.core.commons.progress;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class ExecutorServiceShutdownWatchable<E extends ExecutorService> implements ExecutorService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final E wrapped;

  private final Deque<WeakReference<SonarLintCancelMonitor>> monitorsToCancelOnShutdown = new ConcurrentLinkedDeque<>();

  public ExecutorServiceShutdownWatchable(E wrapped) {
    this.wrapped = wrapped;
  }

  public E getWrapped() {
    return wrapped;
  }

  public void cancelOnShutdown(SonarLintCancelMonitor monitor) {
    if (wrapped.isShutdown()) {
      monitor.cancel();
    } else {
      monitorsToCancelOnShutdown.add(new WeakReference<>(monitor));
      cleanGoneMonitors();
    }
  }

  private void cleanGoneMonitors() {
    monitorsToCancelOnShutdown.removeIf(ref -> ref.get() == null);
  }

  @Override
  public void shutdown() {
    wrapped.shutdown();
    cancelMonitors();
  }

  @Override
  public List<Runnable> shutdownNow() {
    var result = wrapped.shutdownNow();
    cancelMonitors();
    return result;
  }

  private void cancelMonitors() {
    monitorsToCancelOnShutdown.forEach(w -> {
      var monitor = w.get();
      if (monitor != null) {
        try {
          monitor.cancel();
        } catch (Exception e) {
          LOG.error("Failed to cancel on shutdown", e);
        }
      }
    });
  }

  @Override
  public boolean isShutdown() {
    return wrapped.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return wrapped.isTerminated();
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return wrapped.awaitTermination(timeout, unit);
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    return wrapped.submit(task);
  }


  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    return wrapped.submit(task, result);
  }


  @Override
  public Future<?> submit(Runnable task) {
    return wrapped.submit(task);
  }


  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return wrapped.invokeAll(tasks);
  }


  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return wrapped.invokeAll(tasks, timeout, unit);
  }


  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return wrapped.invokeAny(tasks);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return wrapped.invokeAny(tasks, timeout, unit);
  }

  @Override
  public void execute(Runnable command) {
    wrapped.execute(command);
  }
}
