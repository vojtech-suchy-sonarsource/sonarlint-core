/*
ACR-48a2f780e3af42a99473f6698c93c0e6
ACR-f80f95f05c3b4204842d8cc2933d3df7
ACR-471ea298bbf74f498ddc7e7bf0a31c64
ACR-ff771257286546c3acb8339f79000aaa
ACR-a0e18f129bb943308068136f16db02ea
ACR-f10a2f75676643ebaa73deefc90fae01
ACR-ebb27a0e4b504fa48a032520a2fc2859
ACR-d9634290f2ad4500a23a3bc2f2078b02
ACR-e453f32278c04eae853f3bf76f27b786
ACR-74fb8ec20af9453cb2db4ff2dd795958
ACR-ec2bc38b10254f1ba768d6a102e9e4fa
ACR-f2696a6dfb904665834cdaf0beea3f06
ACR-6843c399361a49bdb103ef4f84da30e5
ACR-ad2b32dd967448d183e774702679c407
ACR-7ff0438fbd714e47a23d94bae2e7bdcf
ACR-f75fef1fdb3141d6bb6d612f3dcbbae9
ACR-2f23816c2dda4f24ad332e2947569350
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

/*ACR-b6fa333b66e2490aa64c6ee25188fb9c
ACR-b5bbfe86c2db4e37a0a2929f1f7b1236
ACR-5af00be8001e4e288e7b2b010829194e
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


  /*ACR-52f5812e326f487ca9af50e8d2cc6d40
ACR-bd19a861eaa24f9792dfd2adbc8f6446
ACR-9a6265209e364cbd9dadae19b919756c
   */
  public void clear(K key) {
    var valueAndComputeFutures = cache.remove(key);
    if (valueAndComputeFutures != null) {
      valueAndComputeFutures.cancel();
    }
  }

  /*ACR-4c75dbdcc5414655b3cc6788ea1cb558
ACR-f91f3b13d7994e36ad53049cc35e0b1c
ACR-33deb714f5324a4b973c217a547583bf
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
