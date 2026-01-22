/*
ACR-6dd16bcca2f147c48fc3d159403388b5
ACR-ace2d6979bba46638a8480d99dbe2666
ACR-4e2f930ef56e43fb96586131c19b26c1
ACR-d82fbe3a1d874d65966834006a3d4ea3
ACR-b118bcd05450420290b4b6b24c794947
ACR-73c718e5d87144dd887335f7ff688fd6
ACR-69e860f1851a4ef1b1313eb133d22802
ACR-5ea1437bb68d44ef8dbefc59db05ebf3
ACR-a4a4d72cca0e48cca0980f77fbecafbc
ACR-c0f7e124793944f2b391d919146a6b55
ACR-20391652df1c4332a5c06351d77129fd
ACR-3e9cde0408d64e849fd480ad601df078
ACR-57c6fc4a6e7c45738b5571f04639e882
ACR-24a4ffdba36045a491acf7e7b71be9c3
ACR-e5b8021d6c134b08ae436c0b4e8ec819
ACR-c2e7cee999464dfeb0e2b5bd0267f327
ACR-2a3ee4c398264f45bb7949031799b88c
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import java.util.function.Consumer;
import org.sonarsource.sonarlint.core.analysis.api.Issue;

/*ACR-9ece073919b7470d9eeb6fd9557f9c04
ACR-d9e123d15bf34783af7829f7dcad7296
ACR-9c35b02074e144cda52598e5d97ffd11
 */
public class IssueListenerHolder {
  private final Consumer<Issue> wrapped;

  public IssueListenerHolder(Consumer<Issue> issueListener) {
    this.wrapped = issueListener;
  }

  public void handle(Issue issue) {
    wrapped.accept(issue);
  }
}
