/*
ACR-0d1d2e2fd53b4eacbe1d6aaab533531c
ACR-dab3b50d25074afdaa1562c9089faea4
ACR-dd4e4e7830a3497f861f0b5c9d0ef0c4
ACR-63386d27dd2148189829e134fda2743b
ACR-98daec9ffddc42559eca4c3326e9e85d
ACR-96d5274e7ac64727933fe716f67a20d7
ACR-be4c7eee71ae40edbc37946021c841e0
ACR-98eb91e74ce34f89b0500e1c7088f865
ACR-1ebcd5a48e7d4a77b53e7002440608f3
ACR-7b4bd082e093486bb2f3ab1686eb805d
ACR-9355b4b065bf491a8f8520984756387f
ACR-0ab1907e58cb4e0ca60e498ced53b11b
ACR-4481fe8306f440a09a4905869a059661
ACR-ae656997bf014646828a362a39513b83
ACR-cbfbf58f909a47c2b2393a21d3887547
ACR-75ea68830e754e06946566fb1823884d
ACR-4515ed1db7bf45a38c792507882f3b2c
 */
package org.sonarsource.sonarlint.core.issue;

import java.util.UUID;

public class IssueNotFoundException extends Exception {
  private final UUID issueKey;

  public IssueNotFoundException(String message, UUID issueKey) {
    super(message);
    this.issueKey = issueKey;
  }

  public UUID getIssueKey() {
    return issueKey;
  }
}
