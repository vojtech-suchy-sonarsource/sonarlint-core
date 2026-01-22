/*
ACR-817f5426a44e48d88ff4f9311ea9c7a2
ACR-8f8bae4f47124be59945646b9af3a2c1
ACR-bc8bdc59e38f43f8b999b0145a9ded28
ACR-c545c06391ea4ef9a469fa0f762fba59
ACR-1947e99b8f714c58985c0d50ad54e125
ACR-fda9669b0a9e4a1caa04d7e5ab7296f0
ACR-a8d7f8ea6d6d41bcbeb512b6cdc8ec7a
ACR-b2e54bcb6e2f45fb8efcb7eff575ad94
ACR-c8e6dd9a7f8f4e1d8eaa561a88b32131
ACR-2418e95a565e49158cf83b42ad4175b4
ACR-a1a14b49f6d14c7b9aa1a76827fee09e
ACR-6a01c5eee1a14b5ea3943bf9f1ac71e1
ACR-54bf029d97fd4c99bd556bc8c22425f1
ACR-7cc249ff44e7476b865b77fc95b9e9f1
ACR-294d593e1a884295ade85a8180ce5120
ACR-ab5776e32eb5466c96ffeddec405439f
ACR-1d86ba2878624a8bb7f1bad48df886f5
 */
package org.sonarsource.sonarlint.core.commons;

import java.io.IOException;
import java.util.Queue;

public class IOExceptionUtils {

  public static void tryAndCollectIOException(IORunnable runnable, Queue<IOException> exceptions) {
    try {
      runnable.run();
    } catch (IOException e) {
      exceptions.add(e);
    }
  }

  public static void throwFirstWithOtherSuppressed(Queue<IOException> exceptions) throws IOException {
    if (!exceptions.isEmpty()) {
      var first = exceptions.poll();
      exceptions.forEach(first::addSuppressed);
      throw first;
    }
  }

  public interface IORunnable {
    void run() throws IOException;
  }

}
