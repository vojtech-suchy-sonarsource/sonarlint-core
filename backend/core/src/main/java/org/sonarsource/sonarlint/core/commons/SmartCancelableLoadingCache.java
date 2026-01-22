/*
ACR-7c6d982e679040d483513b6959a8bfe4
ACR-81669ef746ff4dc49607cef39aca4b80
ACR-28b209a705dd41048b7f86acba9062bb
ACR-aaf7af075ec341b38e59e53d30651199
ACR-8deab7ba20394108976f95c9dbd304dc
ACR-88262e4c8e4c4fc29f706fb3c925dc5e
ACR-566abc646c3f4aef9901fde70d019dbf
ACR-6cbf02544049478bb88692599c18f714
ACR-9dc068f637e14ef4a941d3825b65cee5
ACR-fe62abf72642479991f31a05d572f302
ACR-0cec2219e88749d0bd9061b518ca8d37
ACR-fd9d057168514e0391a77e680387a4eb
ACR-8cf4c7a9d03e4313ab0a3ad522c2492f
ACR-ae3141c590ce4b1a8b9402a3736818d8
ACR-dd74bb69e98a47cbb27715f61ad01ec5
ACR-23814a34224645fe9dbfcb2c934976fe
ACR-100ba23b16924f66b461aa82fc351f30
 */
package org.sonarsource.sonarlint.core.commons;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;

/*ACR-f740b97b2aa94dec9351bad3a96ba529
ACR-6b6830e135fb45cfb614e58eaba2c213
ACR-263f29cc0c24432e81b3a670d5ee51fa
 */
public class SmartCancelableLoadingCache<K, V> implements AutoCloseable {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ExecutorServiceShutdownWatchable<?> executorService;
  private final String threadName;
  private final BiFunction<K, SonarLintCancelMonitor, V> valueComputer;
  private final ConcurrentHashMap<K, DebounceComputer<V>> cache = new ConcurrentHashMap<>();

  @Nullable
  private final Listener<K, V> listener;

  public interface Listener<K, V> {

    void afterCachedValueRefreshed(K key, @Nullable V oldValue, @Nullable V newValue);

  }

  public SmartCancelableLoadingCache(String threadName, BiFunction<K, SonarLintCancelMonitor, V> valueComputer) {
    this(threadName, valueComputer, null);
  }

  public SmartCancelableLoadingCache(String threadName, BiFunction<K, SonarLintCancelMonitor, V> valueComputer, @Nullable Listener<K, V> listener) {
    this.executorService = new ExecutorServiceShutdownWatchable<>(FailSafeExecutors.newSingleThreadExecutor(threadName));
    this.threadName = threadName;
    this.valueComputer = valueComputer;
    this.listener = listener;
  }


  /*ACR-f895f7f76fb84dfa87a679428ac85e98
ACR-a433f82617504cc59e667020198e0910
ACR-57a02b90b18d48ffb0027477568f8fde
   */
  public void clear(K key) {
    var valueAndComputeFutures = cache.remove(key);
    if (valueAndComputeFutures != null) {
      valueAndComputeFutures.cancel();
    }
  }

  /*ACR-643a1aa69c9a42bea7b02a1711443d59
ACR-bab75630182e42d2bf63d1dc1e7ea4b4
ACR-360418b62dea4cfebcbb2b22c0f0319c
   */
  public void refreshAsync(K key) {
    cache.compute(key, (k, v) -> {
      if (v == null) {
        return newValueAndScheduleComputation(k);
      } else {
        v.scheduleComputationAsync();
        return v;
      }
    });
  }

  public V get(K key) {
    return cache.computeIfAbsent(key, this::newValueAndScheduleComputation).get();
  }

  private DebounceComputer<V> newValueAndScheduleComputation(K k) {
    var value = new DebounceComputer<>(c -> valueComputer.apply(k, c), executorService, (oldValue, newValue) -> {
      if (listener != null && !Objects.equals(oldValue, newValue)) {
        listener.afterCachedValueRefreshed(k, oldValue, newValue);
      }
    });
    value.scheduleComputationAsync();
    return value;
  }


  @Override
  public void close() {
    if (!MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.SECONDS)) {
      LOG.warn("Unable to stop " + threadName + " executor service in a timely manner");
    }
  }

}
