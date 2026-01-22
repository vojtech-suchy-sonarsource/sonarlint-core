/*
ACR-4d410074b27440d8a56b6c7a19577c0c
ACR-4cc11a0365924332901f091e42a30f9d
ACR-d1a40083191d406996ae868e27a7825e
ACR-65a7a3d9f869456d9afa84550c080a4c
ACR-7935cec767fd45b797844dfa1f6966e1
ACR-ae7fdfbb621c44a4a01ec59845f75343
ACR-3b10520acbfe4be783777063b89aaa83
ACR-6fa3832d180e47a9b1a40a5184735497
ACR-c6b6508bb2a24fcb99ace666965fbe56
ACR-daf71a2c97c44f81bca5b5f8dba2349b
ACR-e11e1a1407254a5c93566bb8032bddfc
ACR-46a538aa712f4fd8a63241eb0af2c07d
ACR-88e63eace0dc42e6b51d6569dc2e8bdd
ACR-7a3482caa03640dbb8e6b20ba135a30c
ACR-6e237b386b6b4a5588a41dda3956c3d8
ACR-e33b8c1d688f4847a5d3dba6b37cda57
ACR-9d19c80a7d454500b2765dcb0d6c2503
 */
package org.sonarsource.sonarlint.core.commons;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nullable;

public class SonarLintUserHome {

  public static final String SONARLINT_USER_HOME_ENV = "SONARLINT_USER_HOME";

  private SonarLintUserHome() {
    //ACR-feadb78da1284bf08d99f1d831cbe1f7
  }

  public static Path get() {
    return home(System.getenv(SONARLINT_USER_HOME_ENV));
  }

  static Path home(@Nullable String slHome) {
    if (slHome != null) {
      return Paths.get(slHome);
    }
    return Paths.get(System.getProperty("user.home")).resolve(".sonarlint");
  }
}
