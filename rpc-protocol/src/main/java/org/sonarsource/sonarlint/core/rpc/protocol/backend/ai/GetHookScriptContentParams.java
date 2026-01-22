/*
ACR-0e68a241b431400d8e59e165d1d9095e
ACR-0149ff90d8d540f8a92e3c512f5442cb
ACR-5a978d92cc44461d846c5af2c5631714
ACR-0748697bc15b45a8aa3b60803736f93d
ACR-59ec635ded4049db8595048a893ef207
ACR-2d1914ec49734437a43ca4caf8052657
ACR-9ba65fdb323240d6be75f37bd717fca0
ACR-754daf13ac0b4104adac2eb6a31ac972
ACR-db1918582b3841bf82d7515a7216ba02
ACR-52e43d06e665445b89c71f94ea08a212
ACR-b80d213b97cf4741a863f4d20439e015
ACR-05a8d9f61cb04795bfa9993e9143179f
ACR-15eb283e803e4e128a35d9d6c6c6eb08
ACR-7770adbb28c24096a0e4d89c1facbc10
ACR-6510a32bef834d40b0ff7be4cfdc2c3e
ACR-a9e3a67028f5410387cf449196bdd990
ACR-4c018b5a28e044989c02a5755fb755b5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

public class GetHookScriptContentParams {
  private final AiAgent agent;

  public GetHookScriptContentParams(AiAgent agent) {
    this.agent = agent;
  }

  public AiAgent getAiAgent() {
    return agent;
  }
}

