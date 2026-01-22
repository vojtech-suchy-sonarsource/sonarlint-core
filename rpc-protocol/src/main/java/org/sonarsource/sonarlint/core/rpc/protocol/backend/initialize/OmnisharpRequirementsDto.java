/*
ACR-8f2c287b8bb84fa9b683d10228e51716
ACR-ce964998d16948b49870c60e1ba0f01b
ACR-fe09504e72ce44999417ade034a6df86
ACR-3d5062a709e14fab8a0ccb0d0085a52a
ACR-17ad9dbf8e05465db43136e0cf481aac
ACR-8aeca9364c324cbeae848184d63d261f
ACR-978fd8fb83fb4c7a852fc1d68267e632
ACR-ce489a6b66f94ee7b011e2f872173ec7
ACR-adbd552ac468480da5c60f8c0d8b438b
ACR-e1e6efdc842a404089a18c375dba2746
ACR-a2c2ec46257745fc94c7a9bb92c6ba28
ACR-74cf1e37ef7941cfbd4d5c39aaf44709
ACR-2251898d2cb043cf9f801ad4fefcb2d9
ACR-ad66bfca26174e34939c94466b459bbb
ACR-9c365d0d96764053a9c7d0aae864a6df
ACR-4be3a840667f489dbb48245b03a4d63d
ACR-f1195ec9ec804e13b11a230c1d7f39e0
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.nio.file.Path;

public class OmnisharpRequirementsDto {
  private final Path monoDistributionPath;
  private final Path dotNet6DistributionPath;
  private final Path dotNet472DistributionPath;
  private final Path ossAnalyzerPath;
  private final Path enterpriseAnalyzerPath;

  public OmnisharpRequirementsDto(Path monoDistributionPath, Path dotNet6DistributionPath, Path dotNet472DistributionPath, Path ossAnalyzerPath, Path enterpriseAnalyzerPath) {
    this.monoDistributionPath = monoDistributionPath;
    this.dotNet6DistributionPath = dotNet6DistributionPath;
    this.dotNet472DistributionPath = dotNet472DistributionPath;
    this.ossAnalyzerPath = ossAnalyzerPath;
    this.enterpriseAnalyzerPath = enterpriseAnalyzerPath;
  }

  public Path getMonoDistributionPath() {
    return monoDistributionPath;
  }

  public Path getDotNet6DistributionPath() {
    return dotNet6DistributionPath;
  }

  public Path getDotNet472DistributionPath() {
    return dotNet472DistributionPath;
  }

  public Path getOssAnalyzerPath() {
    return ossAnalyzerPath;
  }

  public Path getEnterpriseAnalyzerPath() {
    return enterpriseAnalyzerPath;
  }
}
