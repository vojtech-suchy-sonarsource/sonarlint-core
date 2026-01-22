/*
ACR-7ba5a87fe0454ecd8f8b64e97e04f451
ACR-1c86a96d5e36463fa3bd63a63dca8c14
ACR-214c7f4d19464b5eb8e2c74e85ee7092
ACR-0019064c0402410c9151326ca14a249c
ACR-cf22d827487f490fa74fa0eb6966c35a
ACR-d619f660c01d4609ba47398f49fecefc
ACR-9cdae62e221a4ff79e378fcecaf15c74
ACR-384955167c004dd5b8ff09733a8f3b17
ACR-9b2f8ee36d5e40bb9aaf855a615e1bb2
ACR-acff40c7d442422f87718f0cae210ccf
ACR-4a2c2f187ac04b3e87d9a07cc9418ff2
ACR-eea7720ff0f44e54a65bb68572b0737c
ACR-01788511c06748ea88d96c2c0f54392c
ACR-7ea084f757d54bc7b9563b23d8e4298c
ACR-a66663b0b8bf4bb788d8ddff87237110
ACR-2ba6f59def0848e389fad1494ed68cf8
ACR-e498a5f507aa4aecab0d7d13724f635e
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import java.util.function.Consumer;
import org.sonarsource.sonarlint.core.analysis.api.Issue;

/*ACR-6fc2d76a16c248fe89e9243fe0cab5b0
ACR-c2a8693b4b714de389df5def9ad4db0b
ACR-adc477bba22849779513934d30d55fa1
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
