/*
ACR-819cdfa5562048f09166e7545674eef9
ACR-27e8ab8085184842b79f7d25aea0b770
ACR-fc6612b0e79a46cabc631b2ecff8e5df
ACR-58f6853a72de46b089deb78afafab19b
ACR-7ca7bb9ce20840f6a6d1db9db54c52ad
ACR-c55ee3fe3be6419dbc77931607c27e96
ACR-404c733497464bad95db92e85cacb1d1
ACR-de7e51192d1646e487da727dc29b41c5
ACR-08160b2c2bc14ceba521ddbbb672cfef
ACR-e2952afca58f4602ab18da4d0856bdaf
ACR-68c3e8eedd9a45c396aae9cb8cabbd22
ACR-b8d48a9b53e7466b941954db83415f16
ACR-897e878d1a904404bf163c14ddd0eeb1
ACR-85c1c37fed3b4dc5a9065e6658acef8c
ACR-7103473aaca64ffc9c50bebb53ab3f09
ACR-4e02a5dfdf2f4dc5b31c0bee0ee5a9b2
ACR-27a5355f620a490fa3dc97c890d7a579
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
    //ACR-72ca92002d764c38b3c682a3f41ccb01
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
