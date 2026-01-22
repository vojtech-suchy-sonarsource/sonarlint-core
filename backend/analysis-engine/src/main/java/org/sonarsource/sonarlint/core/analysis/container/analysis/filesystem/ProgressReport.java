/*
ACR-2eda792ae61349438ea85ad636f53278
ACR-78a262ff5df94820a6f14c6f90ee6b21
ACR-a2dc4f72a0a146fdbefa318483cc4b7b
ACR-3cd3a51fb478476caa0085249631cdb8
ACR-d81194f8d0734c3597bf5b8b43709a24
ACR-99cc043a1615406e80d02bdc70e94102
ACR-78d0e8bd2ee841749edf8ade87401c58
ACR-999cec71f1924ca496fc3f54238021c5
ACR-4ef95be43ead48b4879cb584ae8f2b51
ACR-7cbe9e4d5e3a428b9a98540e2a78bff9
ACR-b1cd83dc17d64efeb69af77673689d53
ACR-9a7c737e76674a5fbbe547b25c0ad5ba
ACR-0a258aaee58c402a95422b795d7a65b4
ACR-1fd45cc14eb04784b53f74e50ad65d94
ACR-ba96a7872c6346e6acc49461132a311c
ACR-6f9712911ecd468894dfa8164861b002
ACR-2317c559054e4aab9c10395b3a7c1d13
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class ProgressReport implements Runnable {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final long period;
  private Supplier<String> messageSupplier = () -> "";
  private final Thread thread;
  private String stopMessage = null;
  private volatile boolean stop = false;

  public ProgressReport(String threadName, long period) {
    this.period = period;
    thread = new Thread(this, threadName);
    thread.setDaemon(true);
  }

  @Override
  public void run() {
    while (!stop) {
      try {
        Thread.sleep(period);
        log(messageSupplier.get());
      } catch (InterruptedException e) {
        break;
      }
    }
    if (stopMessage != null) {
      log(stopMessage);
    }
  }

  public void start(String startMessage) {
    log(startMessage);
    thread.start();
  }

  public void message(Supplier<String> messageSupplier) {
    this.messageSupplier = messageSupplier;
  }

  public void stop(@Nullable String stopMessage) {
    this.stopMessage = stopMessage;
    this.stop = true;
    thread.interrupt();
    try {
      thread.join(1000);
    } catch (InterruptedException e) {
      //ACR-67d36b9d7ffb4d0c8b5701d221f2a0f9
    }
  }

  private static void log(String message) {
    LOG.info(message);
  }

}
