/*
ACR-487ebc9fd8084996a8a1dd78319c009b
ACR-0058f043f6db49c889d5af62437e3013
ACR-9197490e537c41afb00b31b3027a4654
ACR-8cec8093e9ad4a11bbd87360743fa27a
ACR-5cb0d6de040f47eab5521ca74db54813
ACR-cd6d600f69df4fa7bd3404c613258f61
ACR-8378c812813148ebab7ed58eeaae4d48
ACR-6855bb26451d4faea385ed3400df1b68
ACR-e6892190f87d4d3b9301c9837967b51d
ACR-8be02f31eb0a4c63bebc294a22f8ffe3
ACR-6c9f1c763de04d68adf950f98877caf8
ACR-51720e30680949c9b767b7e5c0878a14
ACR-95072e0a5e5a4ce89f131fd785c88a74
ACR-7c6d1ebb4c664fc497d43a5e25a6ddbc
ACR-dab08b8ff93c4194b774b2fb6be95d50
ACR-e549cf865cb3432ea500fa2958aa3c7c
ACR-28d97e52d42449749ff2f6774c4aacb2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class AddQuickFixAppliedForRuleParams {
  private final String ruleKey;

  public AddQuickFixAppliedForRuleParams(String ruleKey) {
    this.ruleKey = ruleKey;
  }

  public String getRuleKey() {
    return ruleKey;
  }
}
