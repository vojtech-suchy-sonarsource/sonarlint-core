/*
ACR-542aa0a568864183a70b7feb74a73b69
ACR-55b884f9be4c4b2ea7b6290f11859f63
ACR-cf4facc27ed041a090f606ff932e9dd6
ACR-cedc9fcb385e4189b217eaa8fdbbc82b
ACR-4bdbfb2b89a849bfb8b447a2433a5993
ACR-882b8914424146739024990b16b1d139
ACR-7ee104da60ec4b1b884ded522909cf19
ACR-1526baf4ff8842f3b0737039e677fd6a
ACR-60139cc6e2fa496dbfbea98dce287406
ACR-beef53bd88a24247926da4990f79e7fe
ACR-107d06f146874827a7741d257b32465e
ACR-6457e9b46082423bb0d72edf7a67f014
ACR-7ed7fe53142544249f1842f6cfad3154
ACR-b4a03e8a8e094d1eb3ff18dd6f4eca2b
ACR-dc199211511743349c728fedc0f3d3c7
ACR-a76833413c6b4e2ab658f5b00332cec1
ACR-6ca34c05be3243c0b03b21b242a1ce4f
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
      //ACR-8632da5ff38c4d37813bbd4760c52c4b
    }
  }

  private static void log(String message) {
    LOG.info(message);
  }

}
