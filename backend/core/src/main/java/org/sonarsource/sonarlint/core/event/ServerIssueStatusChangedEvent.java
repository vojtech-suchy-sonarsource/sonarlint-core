/*
ACR-2244272db6e141b7b2facb094cd93634
ACR-854769d41ba74325b1c35e815ea34f38
ACR-bf9e13b0f4b84b1caa1a22b901c0466f
ACR-ef76a80837a6467d84c3abf7249f47c7
ACR-db395517fa85401e8b10d01671bd0bb4
ACR-d2f165ca0e644498940440d562ab3517
ACR-ffa68f46b1c4495da0cd220671976919
ACR-fdcbe6760d094713b75fb7e5d77cc5a6
ACR-714f09a53aa543cca8e716256e83e0f6
ACR-7a39c08029c143fc85caedffb37498ee
ACR-1a5d3b6365204ddc91cb938121bf9309
ACR-5d056e9764ba45fe95ee3173e9da86cc
ACR-c8a375ac21d84dafaa76cc5bd024fb2d
ACR-8df05c159c0e4177ad39d2c0a9bf1b6b
ACR-d8cb8224f397457cad4c5592630d2809
ACR-18ee18088ef64cc8a204f87212a7b156
ACR-ff7dea75533d4b7e8aebfd7da230026f
 */
package org.sonarsource.sonarlint.core.event;

import org.sonarsource.sonarlint.core.serverconnection.issues.ServerFinding;

public class ServerIssueStatusChangedEvent {
  private final String connectionId;
  private final String projectKey;
  private final ServerFinding finding;

  public ServerIssueStatusChangedEvent(String connectionId, String projectKey, ServerFinding finding) {
    this.connectionId = connectionId;
    this.projectKey = projectKey;
    this.finding = finding;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public ServerFinding getFinding() {
    return finding;
  }
}
