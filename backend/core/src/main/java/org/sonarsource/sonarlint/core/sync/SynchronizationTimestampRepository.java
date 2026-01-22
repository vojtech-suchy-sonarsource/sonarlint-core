/*
ACR-a2d5776b71ae485b91f5a0f07f2245a1
ACR-d799a480a106445d892485782f23e1c9
ACR-b15678057f944365978db5250a2e2706
ACR-9730937ca12a4c07ad6c4b20a602cfc4
ACR-d3b31c8fd27748dcb1892e5471077da8
ACR-094d454f4f364467a782babf5e3664b0
ACR-580aaf3706864193a21c3733692d96f5
ACR-e57e2fcd22a040fa8234f3e9f44371ba
ACR-579fb692a8544738a8d36d3580267e77
ACR-2e1e79a9dd91436e8967dc2a0329fcaa
ACR-9015bf696b9c40a98fc6d25633270698
ACR-0cf92202d9a74d63b100423fdf154b68
ACR-97b2e72b1f3c4d81ae56d6edfff2e6a3
ACR-341182c2f78b44418b1e467496f285aa
ACR-22f562d48aee4bc383d56016708045fa
ACR-ea97424b77d84091987c7e6ecc9961e7
ACR-a8b03329b1414c3f9200e1f74a8bb5e5
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
