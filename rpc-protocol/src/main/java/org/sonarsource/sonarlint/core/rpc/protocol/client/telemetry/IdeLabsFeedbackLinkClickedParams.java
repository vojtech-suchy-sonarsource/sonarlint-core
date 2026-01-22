/*
ACR-6baca53fdc404903892d87e857570f79
ACR-8c6d9b79ac544161ab363712c04327cc
ACR-cbb998a007f84ab6bc62603fa81d0b92
ACR-4002ab37d7e24cd0ac68226470962a61
ACR-d84063d5e6774a198c3e6a1f6e8a2ce9
ACR-73fb06cd316341feb4d85f1087957055
ACR-2ffdcef3bc5e4540a57210f212d2fe9a
ACR-3a41f4ba5c5447ebbae44e2f95ea058c
ACR-4a117040690e4e9cae537dd9dafdd667
ACR-9bae827c998a4b8db158f032beeadbc5
ACR-904cdf94b5b64ae38ab2b6c0b279b705
ACR-d8f39e38951f4b61b0079b4f6a99e36f
ACR-8d5e9c8061f94c5db79f0dc4a358bd9d
ACR-c2db380e58ad48208f902156ff26a824
ACR-13710e3af7ca4e728b851c3235c0ddff
ACR-53be68a767a843bea225f1e2bb844c76
ACR-2380a84a72f64016981b94ba8610f53b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class IdeLabsFeedbackLinkClickedParams {

  private final String featureId;

  public IdeLabsFeedbackLinkClickedParams(String featureId) {
    this.featureId = featureId;
  }

  public String getFeatureId() {
    return featureId;
  }
}
