/*
ACR-5b5516b7cf634d9c9fe31444054de5a3
ACR-22b03da0566a444ebfd4a9285a15a541
ACR-cdd8c91c2ac24bf38a7e7ce7cc941a15
ACR-318b5f8f951a451abe2bb73f92d4a900
ACR-6a9a695e5188490ab3e65faea1a8e7e8
ACR-3786c7a7e95947b29d37b7d3ed447895
ACR-385bd3010d654e749c924182f6a7b653
ACR-8ba4365ab13f4341a0b1804c16aea6c2
ACR-b90cbab0fc6d4f1ca38734779e9b9c74
ACR-a1f43aa418d8405992dc92a17e70c520
ACR-d7804a524c8e4bdbb2424d44d2b334aa
ACR-95e8be60db824363b1de95d9f133cd95
ACR-931e886431f24df4b1f7719c77ef0fa9
ACR-7c5071583dbc43ebb385e9e8d477f213
ACR-83cbbbe47de44dfb879287a3f07f32df
ACR-0b56ebb49d6f4951a9ba75e532270c27
ACR-09d8719fb68144ce976414be6b5e0ef8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class AnalyzeFullProjectParams {

  private final String configScopeId;
  private final boolean hotspotsOnly;

  public AnalyzeFullProjectParams(String configScopeId, boolean hotspotsOnly) {
    this.configScopeId = configScopeId;
    this.hotspotsOnly = hotspotsOnly;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public boolean isHotspotsOnly() {
    return hotspotsOnly;
  }
}
