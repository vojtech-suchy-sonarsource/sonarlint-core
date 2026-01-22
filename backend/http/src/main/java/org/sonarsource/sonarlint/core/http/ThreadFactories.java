/*
ACR-8fff2e56dc93405abe39969cb4ca8a45
ACR-51492812653646268b8b8ff251ff5bf0
ACR-21cafd7c641d4b7cae18eb08039f0b42
ACR-95f60520abac4cb28c48148d55cdf840
ACR-f4752a44e14d4d74b38719e76e6dd440
ACR-3a8f487a309e4761b924c49441950bf7
ACR-dff38cf54a7e4360aaf3db474b5205fa
ACR-5febbe764e1c4a37afbeeaecee41789c
ACR-353d06862bf348828caef0e2562674c5
ACR-dbf887b5f9f64409ba2c866649d246c6
ACR-c6b59b31df734123956280ae4423fd83
ACR-304cff0caab2469480dd8c8dafcc6d2a
ACR-f3c946ea58d54f75998aff80ffcc6cf6
ACR-bdd2bd16c6d2410a9a93d89cb8bcdc5c
ACR-e6313e3f9430497f9dcb38c627d36d37
ACR-1d34e092a47747b997ee9c92838d2430
ACR-d0635ae3800040f0ba2d9e1b5eb6c238
 */
package org.sonarsource.sonarlint.core.http;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactories {

  private ThreadFactories() {
  }

  public static ThreadFactory threadWithNamePrefix(String namePrefix) {
    return new ThreadFactoryWithNamePrefix(namePrefix);
  }

  private static final class ThreadFactoryWithNamePrefix implements ThreadFactory {
    private final String namePrefix;
    private final AtomicInteger nextId = new AtomicInteger();

    ThreadFactoryWithNamePrefix(String prefix) {
      this.namePrefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
      String name = namePrefix + nextId.getAndIncrement();
      return new Thread(null, r, name, 0, false);
    }
  }
}
