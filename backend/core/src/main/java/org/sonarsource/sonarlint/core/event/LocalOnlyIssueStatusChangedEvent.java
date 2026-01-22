/*
ACR-3fb107bc82a643e7a957721acfa4b41f
ACR-2a307040b8d0453abb28eeee9449eae0
ACR-928eb15cce6242e7b1162163cb3c2a2a
ACR-be23bf27830546f1ad967a324bd809f7
ACR-7cab876ffb87428d86d25d51c3a917d9
ACR-a5d62c58c27141c985e303f404c6c8c3
ACR-ecbf5d3754b04e95b54377266135b6f6
ACR-d305b800ee924877b237a5a30a2d1968
ACR-2375d17e967949689bd36ffad3b92d63
ACR-9998918b8044442d91b74dcfc36ae8c0
ACR-4aaf24ca6bb2485c88ce6107a7fb1994
ACR-35ca3a4f5f57446d9a06b45afdca0015
ACR-1262997e793044c5b47294c3d32df424
ACR-2583dcc0935c451595d6898a430c4498
ACR-03170baed98945edb76a96fbf95fe2b3
ACR-70f319d55b674150a1b908badf9000fd
ACR-cd5fa97ca3094b81845aaa9f6c064eab
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
