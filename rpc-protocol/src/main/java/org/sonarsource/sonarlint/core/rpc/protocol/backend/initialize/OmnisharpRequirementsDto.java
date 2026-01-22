/*
ACR-009dfc13c21545cf8f5fc1dbe204d5c6
ACR-980595f801ed411a982cf74fde0750c2
ACR-5487488a4be14a398d970fdcc233fd8d
ACR-eff4821f75c44e16a99e227ebd167a32
ACR-ca7c15bf25504ba0a1557b9294612d80
ACR-8ac5629c171547e49df6f65ed5d78c64
ACR-8f18638701a24817bdb3cd6ce28020f9
ACR-2f536340eab3405babb1180378987f05
ACR-4cc20b712e67411ca617a8f8c4e92cfe
ACR-9be8cb2278104c2a9dad55a345b9605b
ACR-655645a2da364229af64fe9e7ba16c4f
ACR-9e566a206f5746fbae2c8e5e81c5c95a
ACR-da5f0b49c82b4cb9bb890dd42aab88c2
ACR-22337efa40df4c8cb65b6f9f19173f23
ACR-486555d64c83463fa8a4ae074d41debe
ACR-8679e11d4a33443f85842b46f88b1465
ACR-07beb77f03134b6ca52835e0e0319760
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
