/*
ACR-dcbee8667fb54d8e95a05e4cf02b0d0a
ACR-cfd337789f9842f0948ea5c5cd7cfe9b
ACR-8226918f61154145a93e18c1c45ded66
ACR-9c853513c16b4b76bd798e5d53eae23e
ACR-a5d68e0c88cc42b6874d328e00697e88
ACR-ffb491ec42ae49e5a13e28582e0727c9
ACR-ad959636acd14968bf7718a8eaa55587
ACR-095185f40cad4439b0916a8cf103a598
ACR-e81e7c0526ec4a7f952f2f5a94b23bfa
ACR-9bf42228c3c24d2b8c2132fcaf8dac02
ACR-a5aed3382905442f819ab52b1b84f908
ACR-5a278fc3ce164f158310a036301c1b6e
ACR-5fca0b7f46dc42d8b08fff4f95d478a5
ACR-98f88995f4fc4890a9abcf3cd6c3737a
ACR-a4787a79c08e4b34b54e7a0518e22716
ACR-367e77307e904cdcab4002bf09a0d7b9
ACR-97510dd5e0fa4c47b5468664e3455d22
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class RWLock {
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  public <T> T read(Supplier<T> supplier) {
    readWriteLock.readLock().lock();
    try {
      return supplier.get();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  public void write(Runnable runnable) {
    readWriteLock.writeLock().lock();
    try {
      runnable.run();
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }
}
