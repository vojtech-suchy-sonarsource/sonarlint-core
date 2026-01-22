/*
ACR-184a4e265a784ce7a6a00c56d44b77ca
ACR-99de9eb2ab0141369614e91e2be7b05d
ACR-c4c0e9fcecbb4ad4a53879a9ba79b0ea
ACR-ef475307c6b64ed9bd9ab337b3f1abd8
ACR-08743e24c444467e921b68345f858b40
ACR-0899042446764d53b21889cf153237c4
ACR-76db591011ac439494426ceb2b3eb1d3
ACR-161d7dafbb13457f83964944c59e3470
ACR-c4c0a09f6d614aa7ae0c8a7fda44bae4
ACR-d4b4cc5c69944903ab58d213036057b8
ACR-604cfcad43ae49128a28955b235fc925
ACR-e8df7e28dfa8470991143569d38f9001
ACR-b1743f8f49c04c56b9801c8cd0003a2e
ACR-16ae2fed274442d9b9c15f39431ed16a
ACR-58475f1c6d3a41a58285381bf01d6976
ACR-4cdae8ffd8b94753a763c03d7d17c784
ACR-78536e12b51741bab4464b216fa9d057
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
