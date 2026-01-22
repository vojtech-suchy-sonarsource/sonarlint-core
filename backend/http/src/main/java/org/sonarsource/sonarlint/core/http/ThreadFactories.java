/*
ACR-505b2eb04b634036b83b076a4b270eb1
ACR-27abc19829cd405882928fd8a866f55d
ACR-2962c2af80714015a46b680159470bc9
ACR-8b82d42f16854b4ab7d3872673c3965c
ACR-278fad14c33144208a880e19f690888d
ACR-59fbcc6dc7b44fff9b3b3f92e6b07223
ACR-059361ccd82b44a78b565f12c45ff9ad
ACR-5d93286bd4af47efaad6cef56db9939d
ACR-e54a8721ee6f4a239e843d016225f039
ACR-627f75d01a6441d5a4b4284fff8375fc
ACR-2e4b2486bf514c17b8ef0f20a458b7a2
ACR-23d1ca5067904a9a89ade0bb27a62803
ACR-c8b4a45664da4927bbcdab9c117fbb38
ACR-72ed2287ce5e4ed7b3168bbdf3720bed
ACR-b1c3bc9c81aa40d982560a1638ac9158
ACR-c08897733fff4ca18021640496c498db
ACR-8da18b4fcbf54e868463a4c885fc243d
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
