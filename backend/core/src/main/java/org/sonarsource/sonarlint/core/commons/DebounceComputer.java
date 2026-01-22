/*
ACR-66c2f15e9dd64349a539d72630760152
ACR-013499fec2ee47d49d78a6ba210465ad
ACR-a14453f194454c39bbc7a03d595ab878
ACR-b77a4b733e8d4639a5916d03d37934ee
ACR-69aa0b4793e844f1a3663d7198635a01
ACR-fe92200f69f74604ba164d488bccd70a
ACR-71303b8bdfea4786a6797dd5aece81c7
ACR-cd269090d7e44521b5b24a217538e769
ACR-fbc55f6758434b79b9f5ece0efaf9a34
ACR-9e8e5bc1fa874471b3912f0e45ba318b
ACR-a082ff0d1bb441beb83be628636f80ca
ACR-e485fee9134b4928a5ba3817872781a9
ACR-0e8295675b0d4e199f9c8d9908c524f7
ACR-996aaa5517064aa1ac883d49cb8391ed
ACR-1ce0235e14c349d68ac130c3d072b2a9
ACR-7f33bda2de044a11b933631799d49ebd
ACR-4d7b08e800484abdbc2a707a4ab7a4e1
 */
package org.sonarsource.sonarlint.core.commons;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;

/*ACR-c59eb941307f4a97bc6f1ed91d0f93b2
ACR-268c6574b0ee48f69cd20962b66c79c3
ACR-664db0d78c5b4e4997136fb9700c6485
ACR-a25d9d15bc0948ef9be60c0d66eea7cf
 */
class DebounceComputer<V> {
  private final Function<SonarLintCancelMonitor, V> valueComputer;
  private final ExecutorServiceShutdownWatchable<?> executorService;
  @Nullable
  private final Listener<V> listener;
  private CompletableFuture<V> valueFuture = new CompletableFuture<>();
  @Nullable
  private CompletableFuture<V> computeFuture;
  //ACR-a7a58f2c5f194c4ebf1db0c29e43bcf8
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
          //ACR-5627a833498441fc95817e1a0d82654d
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
