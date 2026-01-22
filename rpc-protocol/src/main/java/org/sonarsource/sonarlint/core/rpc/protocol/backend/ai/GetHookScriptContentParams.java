/*
ACR-27d1fec485b7428da0d0aa447e76c36a
ACR-a58f8486294541edafc94a5a631b9b52
ACR-87f091d8c44f444d8a55cdebae3cc9a8
ACR-99e748b346834c58a1f8fb48b0cdce39
ACR-d7b6dde078f143f1a217c9c9a969b344
ACR-1b895fb79e464249a9774cacf8af21fc
ACR-d951381b84fb44a3bdcba62646ab53f5
ACR-0b0925aeec5841bb897c2f6627fc0869
ACR-a25bdc70cc3f492b906c600e3cc7e39a
ACR-4b6b02cce34648cc8d800fdf7ed52eba
ACR-1aed71eb53b1460fbe13871b9535cc2e
ACR-b489e47e2bde4bd98cac6d550c3b79a6
ACR-65fd9f93f42e4b05ae41e333d8954e6a
ACR-b405291601d94e6ab619e1db45cefbc0
ACR-61fe4ee512d24b13b55c224196b072ed
ACR-65c5d7ca5186442fa5a21ceac004b0aa
ACR-10d4650c0fcd43119c1ec45ea2a8e75b
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

