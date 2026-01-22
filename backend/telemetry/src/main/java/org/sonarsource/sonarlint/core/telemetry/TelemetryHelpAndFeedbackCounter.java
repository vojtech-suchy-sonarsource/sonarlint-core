/*
ACR-e00183e3d7544ea893518d1bd6f3ba6a
ACR-e8b6d69ad8dc40d6a9714b5675f395dc
ACR-381f2e46bee94b4bac28628d988aad77
ACR-0ea2eb1eecc14cae8b257242faf7158a
ACR-3c1fca0993954470aa04e949258a5c95
ACR-f371f03e7721441fb03430e25faa0f33
ACR-3b9da654adfd4265b33b8690e2c7b806
ACR-5c7bd84bb79744f58eec54e5f2beb9d1
ACR-cbd825ff7a3c48f5bea56617b0e4f154
ACR-f99568d3fa854f668310011816bfef6e
ACR-97eaa8a626644c979b60519f059a2958
ACR-428a476bd3a0466a9afdde12a4c24b41
ACR-c85e3d368c1f4011a31d4e5fb45e4a27
ACR-95644d3ea07b4b019f97443af9765489
ACR-992f654ddff440af815823d177a4f946
ACR-aead912d22614e04b494eda007a372a6
ACR-b92ce7fd0b394c7b8f9b3d38345c7400
 */
package org.sonarsource.sonarlint.core.telemetry;

public class TelemetryHelpAndFeedbackCounter {
  private int helpAndFeedbackLinkClickedCount;

  public TelemetryHelpAndFeedbackCounter() {
  }

  public TelemetryHelpAndFeedbackCounter(int helpAndFeedbackLinkClicked) {
    this.helpAndFeedbackLinkClickedCount = helpAndFeedbackLinkClicked;
  }

  public int getHelpAndFeedbackLinkClickedCount() {
    return helpAndFeedbackLinkClickedCount;
  }

  public void incrementHelpAndFeedbackLinkClickedCount() {
    this.helpAndFeedbackLinkClickedCount++;
  }
}
