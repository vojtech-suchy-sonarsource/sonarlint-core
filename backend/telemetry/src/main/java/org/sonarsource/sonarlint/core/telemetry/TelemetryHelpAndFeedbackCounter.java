/*
ACR-2ff22fc0e36c4eedb195045a6a9c6524
ACR-f81db1f069da4f5595595141d14d1f10
ACR-3edaab3b5f6b4782b2443cfb3d7595b1
ACR-7ea4b8e4739744f586424980012c54d7
ACR-b37267aebdd2414ab6218cdcda2175de
ACR-0fa1e7172fe44f9ca5f79cb2af9efb2b
ACR-5ab3f849e59f4feaaaa2af0983316f77
ACR-be223f660a014ced8299ee2d9ab2661d
ACR-6d3ad246833b4a17b76fb0cb269c25b5
ACR-938b3dbc0dd843dda46ec54e817289db
ACR-c194e9bae0f74fc0b1605575e25397aa
ACR-7d4d9133d4ad462cbab6f0ea783a8925
ACR-3dbd4076ac874fbd9a1bdc4fa1ee2054
ACR-2671313942d7472cb4f1a58068e1b74e
ACR-650afa89af024a04a9a60183c19154d9
ACR-3019689625ee4db5ba5d9c7ee235cd9c
ACR-d79cc6c706f24ef59782c9e10bae3f02
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
