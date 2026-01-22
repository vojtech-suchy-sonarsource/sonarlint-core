/*
ACR-05b266c28c7649e4afc6b0cf21dbe3d2
ACR-50a62e4f648c473f9cf77014add601b3
ACR-ce61cb7a3d814d32bfd9cf27bc10c898
ACR-8d0d5023ffaf4206bb6bc494986d7017
ACR-857babc97b754dc295d2c2978a718056
ACR-7e23c81f6ba54546b391e1e27135cf2f
ACR-e1213527a99a4b1fa62852cb52329a87
ACR-2a2e5e8f7d074337bd9a736cfa4b1aba
ACR-5bbd97417d884a44bcc20778d954e31d
ACR-29395feb4a474e33999ffe2d2b62fbd4
ACR-c9b75ce77b844d3ab81387219b507d3f
ACR-aa03b25320cc49a486ebd480401d59dd
ACR-5e58c45598574e84a3d70370304a00ad
ACR-6ad6cfad8a4b477caf33a39c87db9961
ACR-4e86efbe7c0445a380fe50a90e6c7198
ACR-4715c939e366496d8adea0f7a87e11ba
ACR-00674dcb490e4258993c7061fb0c8380
 */
package org.sonarsource.sonarlint.core.telemetry;

public class TelemetryFindingsFilteredCounter {
  private int findingsFilteredCount;

  public TelemetryFindingsFilteredCounter() {
  }

  public TelemetryFindingsFilteredCounter(int findingsFilteredCount) {
    this.findingsFilteredCount = findingsFilteredCount;
  }

  public int getFindingsFilteredCount() {
    return findingsFilteredCount;
  }

  public void incrementFindingsFilteredCount() {
    this.findingsFilteredCount++;
  }
}
