/*
ACR-73d60d1992d14e88aa6144c4a05adbbd
ACR-cf33b62e37f640e28a668fdcc62b6465
ACR-e2e322d59e6a444aaab688e360a5228f
ACR-3f71e3e2d49745c98536ef9e123da89c
ACR-abfe7bc08a164f3bb6e6f6e861309476
ACR-04f0d829e1ca4b38b14ef23e7bd36174
ACR-9e54685db2a448b49efe2962cfe5ec60
ACR-f42d23b2bd654684b31532e571c0dfdc
ACR-c6fcff92eb4c47f292b1d8d19d0771dc
ACR-72474f35dbaa4317ab453fd0e9a806bb
ACR-49adc87550b6488bad1f3d6a6501fb2e
ACR-33f3516ec4934a6b825bf0945f1069b9
ACR-165dca5bd60a471bbd673a5bf9c99425
ACR-6a0ddf24b81b461abb1045861c66c71e
ACR-3be2708113dd4f4aa899dc52e09e5944
ACR-b0ba7a4a4b034888910b57013d7e66cc
ACR-96db635fb1d24bbcbcc02c4247f966d1
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
