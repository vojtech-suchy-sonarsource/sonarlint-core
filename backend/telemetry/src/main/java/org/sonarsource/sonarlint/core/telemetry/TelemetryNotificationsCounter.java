/*
ACR-6ddc02fd3c1e46a6836d973299706933
ACR-39b1cedc94a247a3b920471981e98fbe
ACR-6ce3f9f0fc5046048f1ac0de108b43d1
ACR-5f6e783bf863436bbbfcd9ddb028d401
ACR-7b8892de80f54171b2044d8984fd3324
ACR-4cf3498f14fa4253bec7452e7528163f
ACR-02dc04abc1e84d6eb37b7abb17b2faec
ACR-837a6ea3f8a8494e97e11f558f31c14c
ACR-946b3509f9d94ba8b71104b61d12f526
ACR-43930da2a7744b268539513a7afcd75b
ACR-6738bde05c9347db8b2ad1ce659eace6
ACR-ca5a6d0f68734dd4808fa06f02c2f170
ACR-e6ef0077781b49449385ebca337afeed
ACR-5ad3693cfbef4cb2ad504bc6bcab94cd
ACR-161ae46fc2b046c28313cb805400fcb8
ACR-3403f4d9b4ac4d4ea67612342c938ec2
ACR-74b4494f0f064e479a8db1c6a6625026
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
