/*
ACR-6151b21834354db1b63180cb83304c5c
ACR-9df2e9bcfe9b43679294f99b156d14b3
ACR-bbef87b5c96e47d0ac344a8806ba8d0c
ACR-54927a6e7026426cb9a804d66b8c3925
ACR-630d4404feb84b929e57eb3df63eb30a
ACR-ec95788c287a4fbb8e1fa40f2842d125
ACR-1b5828730b274f6e8e139fbfc93116a3
ACR-f61fbb3cb6f24c5a82057286a82e325c
ACR-b2615fd937044b069761785e88851c42
ACR-e5509636f92b43d583ea83056bbb3f44
ACR-3805232bec7e4f7a9482595eef50ea59
ACR-80150a94dae34b7fbf6bd8d1353f407d
ACR-28454377da42450fa37e81df5d2d871e
ACR-563d646b2ecb4f10b0753ef69937ad9f
ACR-312573f6fc64440eb5391b434923372e
ACR-c9522a26459d4a6985c06ea5379e8d22
ACR-84c0fd5674cd4eecb2c0734231f9692a
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
