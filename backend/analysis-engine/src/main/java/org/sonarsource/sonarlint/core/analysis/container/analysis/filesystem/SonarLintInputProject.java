/*
ACR-f23930a4537a4db8ace12be72867276d
ACR-e926b83de52249168edd16a617267bbb
ACR-2f0288b16ed24b5d80b8abfdaefdd32c
ACR-23f5fc877fda4296a890c77879612c70
ACR-5e8be1a81e23479282ebd37bfe741cfc
ACR-a24567c9b6974592b1cb64949bc4c1ad
ACR-d4a4a798e9e44a899426107e40370bb3
ACR-442931886b0a4a759fefac184c691568
ACR-103cfd740d1b43c3a4163cf062712aa9
ACR-84204bce0ca449eb8b062f6fa19c35e6
ACR-d1aa9ed239e149118908c5e6941e5c5b
ACR-81e3aa01bce24a2aa7acd2b18ff5861b
ACR-31f662ddad194c88896200bd9c7bdffb
ACR-aec75558a536462693244e15b71228bd
ACR-c833bfafea4741c09b22dbde3fc339d5
ACR-076a6e9564444d20911626f545cd95b1
ACR-2c208ebecdc74b7fad42ad49e551f6f8
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.InputModule;
import org.sonar.api.scanner.fs.InputProject;

public class SonarLintInputProject implements InputModule, InputProject {

  public static final String SONARLINT_FAKE_PROJECT_KEY = "sonarlint";

  @Override
  public String key() {
    return SONARLINT_FAKE_PROJECT_KEY;
  }

  @Override
  public boolean isFile() {
    return false;
  }

}
