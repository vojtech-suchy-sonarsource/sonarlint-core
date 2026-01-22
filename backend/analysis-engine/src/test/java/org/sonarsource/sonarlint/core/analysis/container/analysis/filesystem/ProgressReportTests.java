/*
ACR-fb0089119f50402881a88eac5ee8a022
ACR-96e4d7b3e00f4e8d83920f5ee8ea59d3
ACR-21b753d0f413472985177443347fb7ee
ACR-b8b842e446914a949901cf67f2f5e199
ACR-00cc0deca3b64c31be615082b0ea3a03
ACR-b28ab6e8591343fe803c3e7d64c2efc8
ACR-9763fa16b8b446f0aa8b141b0b18c043
ACR-c1733b8a826440f182602cec64b604cf
ACR-20adc47754544c7789d79a57cfa54cdd
ACR-1cd35504bdcc473691304b402c2583fa
ACR-43662cd5591e4932b8c214b36856d7ad
ACR-4dab4cdb10464f76a801bef18e196f94
ACR-2758d813bd88416386e22a8f53943e90
ACR-a0b23b2c411d4d108b20d69bb27e3d82
ACR-f97d0ef60124490f9f5efaf486c0005b
ACR-2e7fe2d7ef324dbdbc79b62fb090d904
ACR-a7c4248d84ea4c53901e10427bf0ffec
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class ProgressReportTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static final String THREAD_NAME = "progress";

  @Test
  void die_on_stop() {
    var underTest = new ProgressReport(THREAD_NAME, 100);
    underTest.start("start");
    assertThat(isThreadAlive(THREAD_NAME)).isTrue();
    underTest.stop("stop");
    assertThat(isThreadAlive(THREAD_NAME)).isFalse();
  }

  @Test
  void accept_no_stop_msg() {
    var underTest = new ProgressReport(THREAD_NAME, 100);
    underTest.start("start");
    assertThat(isThreadAlive(THREAD_NAME)).isTrue();
    underTest.stop(null);
    assertThat(isThreadAlive(THREAD_NAME)).isFalse();
  }

  @Test
  void do_not_block_app() {
    var underTest = new ProgressReport(THREAD_NAME, 100);
    underTest.start("start");
    assertThat(isDaemon(THREAD_NAME)).isTrue();
    underTest.stop("stop");
  }

  @Test
  void do_log() {
    var underTest = new ProgressReport(THREAD_NAME, 100);
    underTest.start("start");
    underTest.message(() -> "Some message");
    await().atMost(5, SECONDS).untilAsserted(() -> assertThat(logTester.logs()).contains("start", "Some message"));
    underTest.stop("stop");
    assertThat(logTester.logs()).contains("start", "Some message", "stop");
  }

  private static boolean isDaemon(String name) {
    var t = getThread(name);
    return (t != null) && t.isDaemon();
  }

  private static boolean isThreadAlive(String name) {
    var t = getThread(name);
    return (t != null) && t.isAlive();
  }

  private static Thread getThread(String name) {
    var threads = Thread.getAllStackTraces().keySet();

    for (Thread t : threads) {
      if (t.getName().equals(name)) {
        return t;
      }
    }
    return null;
  }
}
