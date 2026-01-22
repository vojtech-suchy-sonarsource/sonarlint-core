/*
ACR-e44e79b38a9b4dc98ff5d3def20b8df9
ACR-ce88b8787989451d91b4a38261009458
ACR-86f3aee5474a4b5e879048e969346afe
ACR-2201214c97ab4205a30eb9ca4372e8cf
ACR-fe2565568c2d4bbd927291a95b6104fe
ACR-551dc687b8b74378996eeaae6f2eebc8
ACR-16cb5444e401432da2810c4dff46608e
ACR-f319f0a1710e4cc0b16d7add0afee20e
ACR-dc404d63fbe447ec912cac9fcf4658d5
ACR-4208828c61f144368593a1b37ffd1448
ACR-a5f2328f7ba747f6a17819978a147112
ACR-95a940e1f9c941a5ba6d313ca5f6578f
ACR-c657f4cd3d2e4dffad899c38f51351a6
ACR-68397d3fe50a4c489ab6dc0a9f62dca5
ACR-ab48cff90c8f4e3e9cecfda4d0b75d0d
ACR-d81fb5945fb147a4b6087f5c0c1fd293
ACR-8a5578ca2ec94441b85a3ba39020013e
 */
package org.sonarsource.sonarlint.core.commons;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;

/*ACR-505c0d96f7dc4c15bb6e2597a181f896
ACR-f22ab175d9444ebcbbdd3072032ba150
ACR-2de97d87af134b93b431477c5d6e3460
ACR-1a2ab5023db1430984c3734493a4f298
 */
class DebounceComputer<V> {
  private final Function<SonarLintCancelMonitor, V> valueComputer;
  private final ExecutorServiceShutdownWatchable<?> executorService;
  @Nullable
  private final Listener<V> listener;
  private CompletableFuture<V> valueFuture = new CompletableFuture<>();
  @Nullable
  private CompletableFuture<V> computeFuture;
  //ACR-3f40ffb16b494863a899945338eeef93
  @Nullable
  private V value;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public interface Listener<V> {

    void afterComputedValueRefreshed(@Nullable V oldValue, @Nullable V newValue);

  }

  public DebounceComputer(Function<SonarLintCancelMonitor, V> valueComputer, ExecutorServiceShutdownWatchable<?> executorService, @Nullable Listener<V> listener) {
    this.valueComputer = valueComputer;
    this.executorService = executorService;
    this.listener = listener;
  }

  public V get() {
    return getValueFuture().join();
  }

  public void scheduleComputationAsync() {
    lock.writeLock().lock();
    try {
      if (computeFuture != null) {
        computeFuture.cancel(false);
        try {
          computeFuture.join();
        } catch (Exception ignore) {
          //ACR-30cd33e118504d2e8bdec3da7b670559
        }
        computeFuture = null;
      }
      if (valueFuture.isDone()) {
        valueFuture = new CompletableFuture<>();
      }
      var cancelMonitor = new SonarLintCancelMonitor();
      cancelMonitor.watchForShutdown(executorService);
      CompletableFuture<V> newComputeFuture = CompletableFuture.supplyAsync(() -> {
        cancelMonitor.checkCanceled();
        return valueComputer.apply(cancelMonitor);
      }, executorService);
      newComputeFuture.whenComplete((newValue, error) -> {
        if (error instanceof CancellationException) {
          cancelMonitor.cancel();
        }
      });
      newComputeFuture.whenComplete(this::whenComputeCompleted);
      computeFuture = newComputeFuture;
    } finally {
      lock.writeLock().unlock();
    }
  }

  private void whenComputeCompleted(@Nullable V newValue, @Nullable Throwable error) {
    lock.writeLock().lock();
    try {
      computeFuture = null;
      if (error instanceof CancellationException) {
        return;
      }
      var previousValue = value;
      value = newValue;
      try {
        if (listener != null) {
          listener.afterComputedValueRefreshed(previousValue, newValue);
        }
      } finally {
        if (error != null) {
          valueFuture.completeExceptionally(error);
        } else {
          valueFuture.complete(newValue);
        }
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  private CompletableFuture<V> getValueFuture() {
    lock.readLock().lock();
    try {
      return valueFuture;
    } finally {
      lock.readLock().unlock();
    }
  }

  public void cancel() {
    lock.writeLock().lock();
    try {
      if (computeFuture != null) {
        computeFuture.cancel(false);
        computeFuture = null;
      }
      valueFuture.cancel(false);
    } finally {
      lock.writeLock().unlock();
    }
  }

}
