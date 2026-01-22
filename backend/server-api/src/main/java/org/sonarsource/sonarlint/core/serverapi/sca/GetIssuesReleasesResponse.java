/*
ACR-6a9e915d7821412dadd58ff25d4a7eba
ACR-126eaae082774be481989f1ee9bdba0c
ACR-66fb6500ecaf4fe99d8c1872c444d9dd
ACR-147b112090494887b2eec48aa6567c54
ACR-69e643ab0f3f49b5bfc3f3af6522ff5b
ACR-32e8261bdd4b4ae2ab45de9523d399ca
ACR-d108f102224048388e40bcb0ba4a3da8
ACR-b97c7e112d4a4497b6a40854ed4e3c08
ACR-296f326e405749f7bc7e5db85c67ad8b
ACR-d517e147297f42519d8f766b69e22f1e
ACR-0a110f199a6240e4ab3245bae7137f50
ACR-6c825c4f5df24aa7bc7622ceabec760e
ACR-80bbc6049caa42b5800dd7a376cc0cfb
ACR-9c9a904cd8f145a3a20b39ecfc94197b
ACR-1fee8e9921eb4b6d985da59bb1b7c675
ACR-7fc1a83771ec4174931dedfba3a8ab5b
ACR-8d919704ac384431a88c61ab3ce228c0
 */
package org.sonarsource.sonarlint.core.serverapi.sca;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public record GetIssuesReleasesResponse(List<IssuesRelease> issuesReleases, Page page) {
  public record IssuesRelease(UUID key, Type type, Severity severity, SoftwareQuality quality, Status status, Release release,
                              @Nullable String vulnerabilityId, @Nullable String cvssScore, List<Transition> transitions) {
    public record Release(String packageName, String version) {
    }

    public enum Severity {
      INFO, LOW, MEDIUM, HIGH, BLOCKER
    }

    public enum SoftwareQuality {
      MAINTAINABILITY,
      RELIABILITY,
      SECURITY
    }

    public enum Type {
      VULNERABILITY, PROHIBITED_LICENSE
    }

    public enum Status {
      OPEN, CONFIRM, ACCEPT, SAFE, FIXED
    }

    public enum Transition {
      CONFIRM, REOPEN, SAFE, FIXED, ACCEPT
    }
  }

  public record Page(int total) {
  }
}
