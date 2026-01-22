/*
ACR-28fbd09b90994f9a871db761cf5ec6a9
ACR-8acd53ee85ef450eb7250cc20380908c
ACR-83f30193ba4741c3a3322e0912234412
ACR-e166bcb8ca3e42c2b24b70defd1bb4a0
ACR-c4f110d2517b41c5800161fbb650d6a2
ACR-29729c0e09bc4546871a9800f7c3fcdf
ACR-df2f1710a1c543c09b94c7d8e8c26fc4
ACR-a2745fda20ba4fc99d841193bcac6d25
ACR-eae8c8b8d001460b98a135ca532411c4
ACR-031a4d75a3f04f5892175e396091d9d6
ACR-fc873451ade24c6e90b24d036ed4aef8
ACR-d1dd996d7170450b8fcab54beed99485
ACR-fc266cd2e1fa4816a46f2a8639ccc5b1
ACR-d49d41acce6047d085523292e96cc0f4
ACR-ab171ac94efc4dc3bb38647c11061951
ACR-0bea8273c4534d42887299e0dd87b862
ACR-30b1ed237a304a24a61c6b01b11457f2
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
