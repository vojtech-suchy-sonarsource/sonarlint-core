/*
ACR-6fa77a90313d483390e78bb4c6fa28be
ACR-1ba573fd459440be8b4ca48b16884b5e
ACR-0356afd881044a0abfcbdd19f1fe20b8
ACR-49bde1b145724a6282680ca2d1c31c70
ACR-37689f8de3c94c0bbe1e6c3f5f8bd6af
ACR-5efee5cdce88466e82bd26bab507afc0
ACR-e41d313fce0046279f90446fbc02baa5
ACR-60c8863bf6c64b5588eeb99fc443a6d0
ACR-3d25e10630cf4ab3bb8105dc867b0e75
ACR-9bbfdbfe2b214e9eb7bd14f9b32c9cbc
ACR-faecaa11ae094890a5ae65e0c8b83ba4
ACR-faf11a0a75cb436e8677497d43d2d9d4
ACR-643ba07737294027a4bd983c30f29c2b
ACR-a7ab257d315f4390850c6aefcc355503
ACR-31b1a1a1efc94d9a9475def064cca9b7
ACR-b86807b0828b47909535c866ce03664a
ACR-acbd961510e44280a6ec2e3f397b8c86
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
