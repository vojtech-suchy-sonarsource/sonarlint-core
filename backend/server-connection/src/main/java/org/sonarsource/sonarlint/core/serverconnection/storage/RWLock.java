/*
ACR-91037899f3af4e58ae7e6be8089016ea
ACR-5608bdf6a5004d85bdcb94fc6cf0d5bf
ACR-e74ac5f2bdb7464eb26f9a82969149c4
ACR-bad784d135784fac832dc63d1cefa762
ACR-8922b3bf532e482abe472e69a7840570
ACR-c1296e0cd1be4434a7c3a509ff810e4a
ACR-24fda0e2b2ae4ddea3e136cabbe379f7
ACR-1f881eb6b17042b5b3722be7b0ea446b
ACR-c3244e38589f47bb9754dd43854fdf19
ACR-fdaf93eafe3145f5ae46c32909dc956c
ACR-d876b843e5b94194829acaeca8881b49
ACR-b7c8c38f6c634d33941a136f2c849433
ACR-bd2537a2ab1a48bb891397a5333fc270
ACR-34cbecfe91e14382a560bbf8db1ef24b
ACR-a15e9849268b4c3cae32d619b4039729
ACR-2ea2baad235e433b9c2609f21f56d83e
ACR-2e3007202e3f40309df9eb339fd9d514
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
