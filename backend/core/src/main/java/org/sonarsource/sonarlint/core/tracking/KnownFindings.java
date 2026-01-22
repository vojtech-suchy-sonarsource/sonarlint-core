/*
ACR-509a2eb9d0e54d6eaf72ea59a6f003d6
ACR-846ccdea599743a48ce68f1def7a0d92
ACR-c2f706f5e1d34276b31720fbd0631d96
ACR-833889837b81442a931379bbf3f27c9d
ACR-0b0e154436544daaba1cc69f991e02cb
ACR-4e2363254ee14248a0b3a46ec885c610
ACR-ac97315a0c2d441fa6ecebdd6a8e7faa
ACR-9c0e35e19d2d4017906b856570a9b71f
ACR-531609b7c43d4763ba3bd919094bd090
ACR-e6c711f35216442c90051000e4dc170a
ACR-e5ec9ce78be34561bb162a86b2496f77
ACR-c31d7969b85e4a768d13da52ef392832
ACR-a54d6745537a46a8935797985dfe2a75
ACR-d421c701dc954ddb9e65f2caa318bd42
ACR-0f739cd703e04abdb73675e8817bbdf2
ACR-de2981d455dc46549f157ab4310dd700
ACR-5f48a48e90fa4065ad74d58b018aac41
 */
package org.sonarsource.sonarlint.core.tracking;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.commons.KnownFinding;

public class KnownFindings {
  private final Map<Path, List<KnownFinding>> issuesPerFile;
  private final Map<Path, List<KnownFinding>> securityHotspotsPerFile;

  public KnownFindings(Map<Path, List<KnownFinding>> issuesPerFile, Map<Path, List<KnownFinding>> securityHotspotsPerFile) {
    this.issuesPerFile = issuesPerFile;
    this.securityHotspotsPerFile = securityHotspotsPerFile;
  }

  public Map<Path, List<KnownFinding>> getIssuesPerFile() {
    return issuesPerFile;
  }

  public Map<Path, List<KnownFinding>> getSecurityHotspotsPerFile() {
    return securityHotspotsPerFile;
  }
}
