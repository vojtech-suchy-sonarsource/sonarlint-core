/*
ACR-d7715c7bf3bf40cf92b60799227c9564
ACR-2b359148664f48dab7b1d42b26ce21fb
ACR-86b9bbd42d5c4c959ee6fadc8f34f074
ACR-ac256b1383f34f29b7528ed2c9fdc9c0
ACR-cd4d4c9aab2942f08aabfae5422bd907
ACR-8cd92d8e9bb84c7495512d5bcdc3148f
ACR-6112ffe69734477a95a2c27f1cd895bf
ACR-6b9ed29377e24cb6baaeb01306d798d6
ACR-80178925ee13446690edc6783837790f
ACR-6c96ef95cea94dffb2a0b1e049e71b7e
ACR-cd44ea95fc6644bea8bc8d9cb9ce0a1e
ACR-6867944354bb4da28b333a37514e2a39
ACR-357314c407c444ab98ed498d2045c46f
ACR-870498b51f124040985864c1038d990e
ACR-adc71516348b48abbaccc387df898e28
ACR-1f739bfa21ec4a4bac8ec505911de705
ACR-873f6bdfc7cb42b68230b6835ccff2bc
 */
package org.sonarsource.sonarlint.core.sync;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class SynchronizationTimestampRepository<T> {
  private final Map<T, Instant> lastSynchronizationTimestampPerSource = new ConcurrentHashMap<>();

  public Optional<Instant> getLastSynchronizationDate(T source) {
    return Optional.ofNullable(lastSynchronizationTimestampPerSource.get(source));
  }

  public void setLastSynchronizationTimestampToNow(T source) {
    lastSynchronizationTimestampPerSource.put(source, Instant.now());
  }

  public void clearLastSynchronizationTimestamp(T source) {
    lastSynchronizationTimestampPerSource.remove(source);
  }

  public void clearLastSynchronizationTimestampIf(Predicate<T> predicate) {
    lastSynchronizationTimestampPerSource.keySet().removeIf(predicate);
  }
}
