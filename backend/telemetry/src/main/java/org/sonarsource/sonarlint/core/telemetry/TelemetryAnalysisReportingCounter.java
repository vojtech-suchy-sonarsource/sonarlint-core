/*
ACR-f38ebbb01df64ce38fcc289e80bc5d18
ACR-7d646bbfcb0b473aba90844755c2485d
ACR-b48829e0d3fb44519e231b9becb0e2a8
ACR-ef0517bdef35418ea03415c1148bf830
ACR-0980cda51a834e96a415d6b9ad57ffb4
ACR-738b64db40f24b99b4f0751d50461e0d
ACR-14f565b14d51469a80f89b93b6d4daf9
ACR-bcf068e88bfe4b19829c280c6d571ab5
ACR-6af5d5abf09a48f7841346e5f670a286
ACR-db5fa88399544cc2af531ed14f645d4f
ACR-50361501888449d499642a1a14dbb6d8
ACR-62a09d53ca7f4c12acf9a694fdd72c19
ACR-f0371dac883448649a8b09a763edc4f2
ACR-591370327bb84744bb06ea887cff41f7
ACR-e2b8e140baed45d0a5aa9432b9a41cf8
ACR-beaabfb1815741c691308a9e8cd4ece5
ACR-bd78e49a9a124972a01bd90b2540598b
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
