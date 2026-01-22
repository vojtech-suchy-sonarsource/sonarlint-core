/*
ACR-dc31e62e7c4a48148710dccd0f12bd65
ACR-a4d06653d26d43dca2c40f962a510b16
ACR-0bc856b8bb034805b611bc31e0beda11
ACR-2da8fb04fc9c4b2fb7ce8367204d321d
ACR-aaaa841009da49dbb6e831c222f80b0e
ACR-1cc4d76c1a7140b1af6bcc71db17142e
ACR-7d405a0112874a9a98b477b87216bf24
ACR-9da5f5b79ff8436994b2eda5cca85f26
ACR-a341bbe2fcad435d882cb82a98581f9a
ACR-2d6e2c2fa78248edbef2e22fbc9a51a8
ACR-752bbb2aee644b0c9624afba34746849
ACR-499b6cd0614c419887f1f57f056d0485
ACR-3918d74995b041b098b485acbd18da66
ACR-2bd7d7d29275485ebc91075aec625e90
ACR-bddb692bde4941b685530cba7bd39105
ACR-a4ee6dde04be4354ac86afefd646d0b9
ACR-7557aa9e06e440638a7b06729b2cc32c
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
