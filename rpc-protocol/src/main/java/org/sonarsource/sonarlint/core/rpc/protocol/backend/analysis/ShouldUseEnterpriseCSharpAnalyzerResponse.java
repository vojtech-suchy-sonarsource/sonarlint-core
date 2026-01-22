/*
ACR-cf99b4ad58ee46739cb51e259417dee9
ACR-8dba6e88a1664a80ab8aafc6748af77f
ACR-45206cacd557439d8a1eacda2f9660f7
ACR-616370c5741a478cac0a1981cc4f83bf
ACR-ea3a95a7d7864a199b925a52f5c48182
ACR-a08b626d05c340ad9d57f4184d5b9abf
ACR-b9edd06b79404ee6aab93f1bb7db0927
ACR-e2a86f45b4d24120a2105e9edc1819d6
ACR-751698075cc7454ea2c452c141bbc343
ACR-bc56cf27444f4221b0eb26b5de531506
ACR-3789e3ae460447da9cbb2b3372259766
ACR-a12c45fcfda54dc48a505e0f8b00d144
ACR-74d23a9211ee4710be89243d113bae87
ACR-b28b11b983f243c583e21a30934c6fe5
ACR-5628f156e4fd4a1bbed19b1100a9a0f6
ACR-51ed5d0e617f4372bc176fd2c7ffd9da
ACR-f2752ab37b044813bde0fd6292e93b69
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
