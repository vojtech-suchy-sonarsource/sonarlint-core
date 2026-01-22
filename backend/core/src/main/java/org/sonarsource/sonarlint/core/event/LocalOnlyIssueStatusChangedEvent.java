/*
ACR-b9653176d4024c928a43757e52081f05
ACR-0527431cb8ad412897fc0b471101de8c
ACR-4f73da0c40b1422cb16f3c5ddc7674b1
ACR-31cfc31777e745188033c7c3685df9d9
ACR-28ea7e23031d402abca0287a137a60fc
ACR-d406ffdf63de4cecb5b41db321775962
ACR-aceb2b457d1f40d89894ffdbceca755a
ACR-6c8366c6fb6e4f94b0ec78820f227988
ACR-4c488b7fe05d4252b7789cc4e4f922f1
ACR-7643cbac4363405c902917e765491365
ACR-dcfd0bb9977c48d89e77fc3bbdf00d70
ACR-2dc0ba748e4c455a946994815434ef45
ACR-aefd88db6b3d4338b83c368e64a9eb35
ACR-c41e2dbca4434a149a4266c3f7ac0cf7
ACR-e8483e14219547eb99f03abbb19a1760
ACR-996a5809a9334739af1ae89020fe97cd
ACR-943eda41de8a49d3973082d44b5ecf72
 */
package org.sonarsource.sonarlint.core.event;

import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;

public class LocalOnlyIssueStatusChangedEvent {
  private final LocalOnlyIssue issue;

  public LocalOnlyIssueStatusChangedEvent(LocalOnlyIssue issue) {
    this.issue = issue;
  }

  public LocalOnlyIssue getIssue() {
    return issue;
  }
}
