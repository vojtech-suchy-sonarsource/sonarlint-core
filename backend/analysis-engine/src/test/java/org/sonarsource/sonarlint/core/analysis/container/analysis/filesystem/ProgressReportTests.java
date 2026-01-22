/*
ACR-eee6184e62354bba95c6786a8f75d0f9
ACR-cabc1663ceff47efa5c4061e293dd0e0
ACR-fdbcaf54914c497e9a9f41a0c7c4d75d
ACR-6aabc57160fa4688bcd8e28ded87f9b9
ACR-bbc14e4c5a564683b93c0ce4bf03c046
ACR-67a076de1e234e2c9c71c90b1622a354
ACR-647133e5c1f346b0b5eba41e73308ec5
ACR-cd9a44ca11a94c698224265c7ae7bcb2
ACR-b8bf754129d74c3a874c2d373e32f4d9
ACR-7470d0b719a841b481277f40bbaa508f
ACR-7a80f40d47ab470f91dffc44d7b11824
ACR-1de052d054c9465a9d01296759d5ed45
ACR-663b768479c546d08327208e28d0ae73
ACR-088aa154be5849618e44cb9e90bc8d48
ACR-6486427070b24ab9940a5f98b0323a0d
ACR-f80ca7c309924c13baccf034329809ac
ACR-d0f8eddd58a64602ac0dc188fa936735
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
