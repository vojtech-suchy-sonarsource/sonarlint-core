/*
ACR-26e499fc712d4f7ba0e0734d09c46a81
ACR-c4620c1979e6409c92a1c25b223b91cb
ACR-ed7bad3435624170affc7bb94e18e74f
ACR-c9308a1560b545e28b049a627cfa58a0
ACR-572d8873c7124462b2b5c9171e0133ae
ACR-04ffc84e520947b19af77adc26328508
ACR-9a24afa586d9491f9eaaaff9339ad5f4
ACR-32e837fe4a914c168edc159faaf5836b
ACR-26167d2998aa4a02b23f111e88769401
ACR-1e56f51a8bde47a0acca719396e4ad1b
ACR-4f71214435824e5a89963553f584e70e
ACR-ff0d156ca4564d2fa93473ebbe3793ca
ACR-126f5dd1ed644fdbb83bac18b28c5673
ACR-c40a3caa349d4bc7bc111bd0a4e19574
ACR-18e9f260574041fa8e9ab926731e5b80
ACR-ef49dfa3217e465fbe7d7d13dee8544d
ACR-9a520dd0d70c42ad8e5cb15bd67a5031
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
