/*
ACR-473c2efa994b4e7aaae16c3eb9b03cfc
ACR-f669ce3c1bbd46ac973886fbebd659bd
ACR-9bf43fd9ace1467ebaf343d6e63c1537
ACR-ff7f57cf90e24959bccd4d8c1e3f9d25
ACR-b56cbc6307c14b698d915e67a93288f5
ACR-acb196b9cb714d7690382b55f55585af
ACR-4112b92207b345c99dc9a9cb89576d37
ACR-3740d22bbf8343ef92f6feb0c038e470
ACR-9f8b7daccbc349308de39499e5d8cbd2
ACR-993ac2fba32a4b39a1ce8767c9e60957
ACR-271388eb6c864372bb46bb2de415c649
ACR-3828084704274db4b9eadec082ab86e3
ACR-8bdb0ad34dca4f9089de4b924756b2a6
ACR-d9bb2c31ca7b486fbc5e64ac2da609eb
ACR-f00878d04bfa46d68795ce628de2b275
ACR-917a7e9d893741ae8829be3047e10246
ACR-4247b7450a8043beb701a58bbcf697a7
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

public class GetRuleFileContentParams {
  private final AiAgent agent;

  public GetRuleFileContentParams(AiAgent agent) {
    this.agent = agent;
  }

  public AiAgent getAiAgent() {
    return agent;
  }
}
