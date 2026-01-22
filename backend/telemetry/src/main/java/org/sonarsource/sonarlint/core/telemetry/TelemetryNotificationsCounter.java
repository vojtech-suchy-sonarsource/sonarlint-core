/*
ACR-826fba04a2084260898d7d8ea0c75f28
ACR-6e6fd0e1d79840c3b81fddca64ee3876
ACR-a86ff1dfa77f46959e2df5281877060d
ACR-308b0669e5364a7e8874749f8079840c
ACR-257a7f4af19645b28ec9839916a7ff29
ACR-41a344816a5549bb93335a4eafb6d33e
ACR-27f632d56a4e4ef8a75d69d550bd3071
ACR-5d00909e3fae417c96d5df01dd04e82e
ACR-bcfde798600e4f82aa3ba91527a2a304
ACR-e9e5acd7515a4d53853657d88c1b2275
ACR-5824abc2051f43d09b9b5b8b3d7d75a4
ACR-cae515ded851443e9e694a7761a24932
ACR-40f6566c903a422da631a463ec26da5f
ACR-5e810daeda3f422b876400503a832b8e
ACR-01ed4144a1e640c3b9c4e51df8b5c2ef
ACR-4e4ea911e0ac4c7289eb74d1debe1619
ACR-4ecb16295f4b4cd3830c38f95bc71ba6
 */
package org.sonarsource.sonarlint.core.telemetry;

public class TelemetryNotificationsCounter {
  private int devNotificationsCount;
  private int devNotificationsClicked;

  public TelemetryNotificationsCounter() {
  }

  public TelemetryNotificationsCounter(int devNotificationsCount, int devNotificationsClicked) {
    this.devNotificationsCount = devNotificationsCount;
    this.devNotificationsClicked = devNotificationsClicked;
  }

  public int getDevNotificationsClicked() {
    return devNotificationsClicked;
  }

  public int getDevNotificationsCount() {
    return devNotificationsCount;
  }

  public void incrementDevNotificationsCount() {
    this.devNotificationsCount++;
  }

  public void incrementDevNotificationsClicked() {
    this.devNotificationsClicked++;
  }
}
