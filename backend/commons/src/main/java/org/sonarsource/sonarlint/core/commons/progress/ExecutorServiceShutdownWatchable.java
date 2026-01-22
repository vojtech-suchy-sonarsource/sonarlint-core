/*
ACR-fb471183ee3f4a379279e8737271b68e
ACR-803b954dbe124a9fa6a8ec5562f28f89
ACR-19c0141af9a64d8eb03dc986b72b1285
ACR-da79d5b6d50f4a7e9b34e9356053fd15
ACR-0cc3cfac84914f309e8e95380576f106
ACR-bff98516811a434095c4beabc4f57a71
ACR-c815dad59e994f76af21b5bad0c15863
ACR-76e6714eb39d4443974bef236e3b8c31
ACR-1aff1be67e1549b4be7632346cae1686
ACR-0a43125e95c6444899346eadcf2b1f8c
ACR-619ce0c5d59944c29362227eb9d8e4c1
ACR-e3429660eeb44e60aaeb41d212e89ff5
ACR-25cb6ea331814e7c92acd5d0854ca2ca
ACR-749bf413229548c880af845caa1b455b
ACR-a4f3820f36a24f39845b693b4c475d25
ACR-6c4118b2b7a5455a80885d2ac8dc0940
ACR-5bada5507a5245e78c1667e30aaf7361
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
