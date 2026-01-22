/*
ACR-06e1219cac754b259789ba0759162935
ACR-ea6f1db189a440d687152653ef996cd5
ACR-43bf8de54776469bad508d0223c9d12f
ACR-0336aebf479140ea88e337840c68699b
ACR-92f3e22c6b5541f580fb8a4b52157d73
ACR-d7874623196a43bb917ed5619b7b4be1
ACR-1c12383aa40847b8b4fda5c0fff7167f
ACR-6974594ce85f4333af1230f483f457e9
ACR-edd10f98c42c4566bfeabb0906cef2bf
ACR-a1aa00e3ae154ff09f6447e266dcca42
ACR-f5d7acea139041a58c88b1eec8fa4489
ACR-2c7e581eafd5431faff882465d42f2e0
ACR-4ffaa7037ccc43dd86a34250e4651c2c
ACR-6e55549ce61a4914a8cf733717fa011d
ACR-cf351436416c4b9fbf5910c2b2912244
ACR-b0f7c342a8df4c258de42ad677c0c048
ACR-f4c2fab68ed144b1ab6bc7a78e6cfc4b
 */
package mediumtest.newcode;

import org.sonarsource.sonarlint.core.telemetry.TelemetryLocalStorage;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class NewCodeTelemetryMediumTests {

  @SonarLintTest
  void it_should_save_initial_value_when_focus_on_overall_code(SonarLintTestHarness harness) {
    var backend = harness.newBackend().withTelemetryEnabled().start();

    assertThat(backend.telemetryFileContent())
      .extracting(TelemetryLocalStorage::isFocusOnNewCode, TelemetryLocalStorage::getCodeFocusChangedCount)
      .containsExactly(false, 0);
  }

  @SonarLintTest
  void it_should_save_initial_value_when_focus_on_new_code(SonarLintTestHarness harness) {
    var backend = harness.newBackend().withTelemetryEnabled().withFocusOnNewCode().start();

    assertThat(backend.telemetryFileContent())
      .extracting(TelemetryLocalStorage::isFocusOnNewCode, TelemetryLocalStorage::getCodeFocusChangedCount)
      .containsExactly(true, 0);
  }

  @SonarLintTest
  void it_should_save_new_focus_and_increment_count_when_focusing_on_new_code(SonarLintTestHarness harness) {
    var backend = harness.newBackend().withTelemetryEnabled().start();

    backend.getNewCodeService().didToggleFocus();

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent())
      .extracting(TelemetryLocalStorage::isFocusOnNewCode, TelemetryLocalStorage::getCodeFocusChangedCount)
      .containsExactly(true, 1));
  }

  @SonarLintTest
  void it_should_save_new_focus_and_increment_count_when_focusing_on_overall_code(SonarLintTestHarness harness) {
    var backend = harness.newBackend().withTelemetryEnabled().withFocusOnNewCode().start();

    backend.getNewCodeService().didToggleFocus();

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent())
      .extracting(TelemetryLocalStorage::isFocusOnNewCode, TelemetryLocalStorage::getCodeFocusChangedCount)
      .containsExactly(false, 1));
  }
}
