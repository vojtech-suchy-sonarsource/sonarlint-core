/*
ACR-23291287456a4133b65e4613aadab5f6
ACR-505814f518e14b1f82efb04277a27d08
ACR-a3ac9dd619304815a93b06866bb65e6b
ACR-06a583760f2e4f53a6a4705fa5ee278b
ACR-96e3d01686eb4ba2bcb8d9c695153379
ACR-801c4a148c42473e891578db6f86c5c6
ACR-8f9954431ad54864b8e63718b1067851
ACR-5fe596fc3758417f8ba298660ce02def
ACR-c7bd189be65c437ab25e27a7f71e93bc
ACR-33eb56ce40824850ad9c4039bc5e4cd9
ACR-5bd0411fd7134cc0a1a13f16c91b394b
ACR-1ad2a72d63dc4da1b8f35754fe7c528e
ACR-66bbc0153e184efcbf368af95ba1e666
ACR-045ab4d704084a2891debfa0dc871bc8
ACR-38b4073dfbac4f95bb610637177392d4
ACR-f566befc6eb74eff995cdb3416db91eb
ACR-6aeb960b17e047cc97720d9e6b74b34b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class AnalyzeVCSChangedFilesParams {
  private final String configScopeId;

  public AnalyzeVCSChangedFilesParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
