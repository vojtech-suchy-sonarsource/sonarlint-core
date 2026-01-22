/*
ACR-b486e1caec5b4e7fb051d2fb09fcdc30
ACR-ffb44b5b9c2c45cd88a98f6c7e8c9429
ACR-598c0e54c5614447b70d157d3d5a4964
ACR-257ec92b557d4a29a7e99cfea236279f
ACR-c4345f1df210402694a2443bc2bf9898
ACR-e5ff15efda664bc490844a0ad9c30644
ACR-72f5955c582c43428d087ab8fceff993
ACR-c733ccdddacc423d97526ccf517d28bd
ACR-44f507a48fa34be0b66738a2118d2815
ACR-735bc0eb918c44eb818b0b5848619dba
ACR-7159bbf47ec1400abe11b0f3e5efa425
ACR-3273fc3822e2401294c1d4aff2533973
ACR-51c0fa59d47e419f8689c9ab6c44b7ed
ACR-25aa745aab1a4bd0b98b5d10b0df098a
ACR-2920c3a45bbe42e5819e819740caf130
ACR-78d75af6db5249baa14b7cdbc0c5cf19
ACR-42382486bdb747bd8ba3410d25521292
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

public class GetFileExclusionsParams {
  private final String configurationScopeId;

  public GetFileExclusionsParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
