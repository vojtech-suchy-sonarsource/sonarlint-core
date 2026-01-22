/*
ACR-3826769d23824ca79419313307834d19
ACR-7b33b295c6354857b01c2704bde83582
ACR-75c40d324adb41c0bdd7597f044dfdd1
ACR-ff17e08ad3a84873ba21816498739c08
ACR-462d3b7cad094f37b5299f647889c592
ACR-c5b3ebf413184fa7a6b2ed861852ec87
ACR-cc809edb728140b0bb0e0b7fa972ce97
ACR-12af5478554748c0943cce42998df10d
ACR-ca58df6764994a4eba53b372bb1660c0
ACR-a62e588a02f343169dab40730bbca33d
ACR-bae026b8d6e2408caaf10122dd414746
ACR-7c5cf7080c2d452495d6596c3a8b4e6c
ACR-868f2cd796644960881308b5cbe4af40
ACR-34630d248c3c454d8cc1b1573d1208f1
ACR-1f64ad586572419db2116f9f0a5d0293
ACR-0e50a0b33a1d412fa70ff63ca9b8f383
ACR-4bce1608ab654fea8725742a58c86e17
 */
package org.sonarsource.sonarlint.core.commons.progress;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ExecutorServiceShutdownWatchableTests {

  @Test
  void should_cancel_all_monitors() {
    ExecutorServiceShutdownWatchable<?> underTest = new ExecutorServiceShutdownWatchable<>(mock(ExecutorService.class));

    var monitors = new ArrayList<SonarLintCancelMonitor>();
    for (int i = 0; i < 1000; i++) {
      var monitor = new SonarLintCancelMonitor();
      underTest.cancelOnShutdown(monitor);
      monitors.add(monitor);
    }
    underTest.shutdown();

    assertThat(monitors).allMatch(SonarLintCancelMonitor::isCanceled);
  }

}
