/*
ACR-a4ef384a7b8b43ea8a3a7b1eeba570fd
ACR-dc067a4c90574b9888c890c558310f12
ACR-cfcb7d815ad24a77af33d1c527544380
ACR-cd9a0e630e504826b4dc314fffaf382c
ACR-336f42dd234344758d9fb0681d5da3cb
ACR-8e1bac01916545afba27189423ee40ee
ACR-7994fcd6eaca403797750ebb58519c07
ACR-4d5d6c3cac0a415cae1f27f9d3fc9642
ACR-daff4a7ed51849cf8c609d4bd185aa8b
ACR-7659f0e45f0b4dd4a26d525b12d12306
ACR-17be48962d234f4cbe3140c64729f9e1
ACR-de379cdc4ea740d1b6eca1fbaa4c5777
ACR-b86b1f92f3af4cc1a68832b3a1cb077e
ACR-d99cc1f7e7524749be29d239e25150d5
ACR-eedcb6d5931f4fc4bd95bfc77a51c512
ACR-96ebf919e75e4beb8de6221b31143cbb
ACR-e65855f5c4df4d43bc7483becd7199f1
 */
package org.sonarsource.sonarlint.core.telemetry;

public class TelemetryAnalysisReportingCounter {
  private int analysisReportingCount;

  public TelemetryAnalysisReportingCounter() {
  }

  public TelemetryAnalysisReportingCounter(int analysisReportingTriggered) {
    this.analysisReportingCount = analysisReportingTriggered;
  }

  public int getAnalysisReportingCount() {
    return analysisReportingCount;
  }

  public void incrementAnalysisReportingCount() {
    this.analysisReportingCount++;
  }
}
