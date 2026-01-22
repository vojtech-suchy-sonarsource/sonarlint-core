/*
ACR-f7fcaea94d2848a6994281317d8d7d91
ACR-f28976fe78c24586aec241b976d9d9a5
ACR-10a447c1db4a491dbf1bad63668c8e6d
ACR-61625c6ff6ca413f857f00599e9a702a
ACR-f636902354eb4ca6869af0d4d99ec3df
ACR-74b6ae4e3397481b8991f1bbce62abd3
ACR-d6658a685dbb402e8f19e554af50c5f7
ACR-496fe14ebf83465aa2b24c949ad8e9bd
ACR-199896446121433cbf9eb2351ccfd3ce
ACR-9785ee339e644a69a707845606bf5595
ACR-a43424e55a6c468099f7d1f5296777ef
ACR-e98d41658d6044d4999ed14d29f67b36
ACR-3ef183bf46874522ba91b91f03448e50
ACR-f20abeea7cd54d18aaad8860cff58616
ACR-f56ab621ed7b4c109ce30d0d88b7fd1c
ACR-8fe78e3254714fd7b488b9037d15f1ce
ACR-019b2eccede8486a9b1d7316af4905f2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.util.List;

public class GetSupportedFilePatternsResponse {
  private final List<String> patterns;

  public GetSupportedFilePatternsResponse(List<String> patterns) {
    this.patterns = patterns;
  }

  public List<String> getPatterns() {
    return patterns;
  }
}
