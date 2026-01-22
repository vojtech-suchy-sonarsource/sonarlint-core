/*
ACR-d2b19d670d404280820ce34ef494fd0d
ACR-b83483c40e9a452abfd69af1dc86b692
ACR-120eb86777c84dcfb37b8d6208fc3483
ACR-516bab6d216741a3a58fa90df4ae6f4a
ACR-f68f343d79fc4c8785b4ee534dbcecb1
ACR-dc6674734206404da5daf782794c2d8f
ACR-db30df9ef2ff47359c983d25d3a04ea1
ACR-10b6a1571ad94d22868d60f560110ff0
ACR-1d959a9cb61244daa9e4824ac135980e
ACR-e6fa1b67805e44798c31f137bdf83fb8
ACR-67cff537f87a4bda87ef5e8fede0cbe4
ACR-2d43ef9da8bb423796090d604541d97a
ACR-f2ac2d88a1244ff9a8b6a63d6cda37ba
ACR-f9a68ce286514a018b1f1f08ee29fb4d
ACR-b082b4a4fb7f474894aa6df8033f8f5a
ACR-973d694ee5d94c93a581bbe088149040
ACR-f6c6f7a75f684675a9f1b8c29930dae0
 */
package org.sonarsource.sonarlint.core.commons;

import java.nio.file.Path;
import org.eclipse.jgit.ignore.IgnoreNode;

public class SonarLintGitIgnore {
  private final IgnoreNode ignoreNode;

  public SonarLintGitIgnore(IgnoreNode ignoreNode) {
    this.ignoreNode = ignoreNode;
  }

  public boolean isIgnored(Path clientRelativeFilePath) {
    var normalizedUnixPath = clientRelativeFilePath.toString().replace("\\", "/");
    var rules = ignoreNode.getRules();
    //ACR-6bc9ceab995c443ab56980c8e1274e7f
    for (var i = rules.size() - 1; i > -1; i--) {
      var rule = rules.get(i);
      if (rule.isMatch(normalizedUnixPath, false)) {
        return rule.getResult();
      }
    }
    return false;
  }

  public boolean isFileIgnored(Path clientFileRelativePath) {
    return isIgnored(clientFileRelativePath);
  }

}
