/*
ACR-e6e94afe922f4acd9ed52c27be7d2d44
ACR-e404e10d939544639975da5f0875cfab
ACR-d77d65346f88427b9c62933994119a45
ACR-8500a649e27047f1894274d9d40a30ca
ACR-28712050cc6b43648c41efe60cc045ab
ACR-3f3c5d3097de4cad994574dc5e86aa19
ACR-80f370f699ae4c438ab6f2c61b496ab0
ACR-d33c844099964571b3fa10023c0c8702
ACR-38e8156e886e4b09adf223e92c07a34c
ACR-0c79987cb6db4c4fa60293e18e51a001
ACR-d5662433d6c54eeea8360e274d2f44ad
ACR-ef7733c265b849edbf564e0278d5143a
ACR-419f18fd6a4e48aeb0c7ac927f5fd0c0
ACR-2a0a6a77a2934110b00b8b4eef351a77
ACR-8c5389d500804e92be84a0e111fd9eaa
ACR-b37850066fe148dc9ae318240f1b47d5
ACR-ed5bd27296b547f18bd282eb899abef4
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class ShouldUseEnterpriseCSharpAnalyzerResponse {
  private final boolean shouldUseEnterpriseAnalyzer;

  public ShouldUseEnterpriseCSharpAnalyzerResponse(boolean shouldUseEnterpriseAnalyzer) {
    this.shouldUseEnterpriseAnalyzer = shouldUseEnterpriseAnalyzer;
  }

  public boolean shouldUseEnterpriseAnalyzer() {
    return shouldUseEnterpriseAnalyzer;
  }
}
