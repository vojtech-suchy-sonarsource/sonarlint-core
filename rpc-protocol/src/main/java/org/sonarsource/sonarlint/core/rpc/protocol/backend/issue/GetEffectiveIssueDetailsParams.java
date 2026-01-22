/*
ACR-5bf12d9552a64b3a988d00a83d1e7bc2
ACR-2a055b94ebc6448a82bc19428989f3c1
ACR-fbb197fd4d3542609b82431611babf8e
ACR-0025557a03ad483c802ebb624ccff794
ACR-ba5d7a2686a64c6cb210760318fb5e67
ACR-b9fd0d49984c431c9ad3d5ebf229e333
ACR-4e935fe132bd4c27a02d3e5df6134a3f
ACR-c8f30b57183c420886158686bba5d263
ACR-1fa8e6f08273460f8cc7d7ea78b4e74b
ACR-1c60f1a7aa9f4d8ba002f07521ec8858
ACR-ddbf550328674b00937da220d202f0d3
ACR-3cca942095784683a85344497022dce2
ACR-8237e9ccd4474a509496005f735a6c27
ACR-2817a60e72aa48c08b5aaa101ff3173a
ACR-507489dfc2c54674bea8731d665631fa
ACR-331b7ec2facb40d79cc6113ab2263c39
ACR-eddf23f0ba1e439b91cf3ad84f5e4c5f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

import java.util.UUID;

public class GetEffectiveIssueDetailsParams {
  private final String configurationScopeId;
  private final UUID issueId;

  public GetEffectiveIssueDetailsParams(String configurationScopeId, UUID issueId) {
    this.configurationScopeId = configurationScopeId;
    this.issueId = issueId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public UUID getIssueId() {
    return issueId;
  }
}
