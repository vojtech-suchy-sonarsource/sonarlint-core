/*
ACR-88d9460649854a5dab9ed42589057228
ACR-49b523252c2345b4990c6a87e93e410a
ACR-9226174c8db14f05b091dc9cee1dfbee
ACR-554cd151037143b79ce6fff51c35e92f
ACR-53079fb30e144abfa8a9d27910126a27
ACR-a2eeb584b7c34b958fb2a410666978b0
ACR-f0c05bb200204a33bb460b68538d4618
ACR-0cc070d4cb5743dfa2e90acbbb80b798
ACR-643c842cb9824bc78b55e464897e6ec6
ACR-f1993bce763c4f6aa475fda5437dc6a7
ACR-735bc6d1746b44eeb586783e0f4e2b14
ACR-f949d13c4ef3457e8df37dcff3e582a5
ACR-51c13405de494513adf24d1d458cdc21
ACR-76c300f4fa604acba163deb18beee4bc
ACR-9a0e4b2edcac46e1b13c85de261d7bbc
ACR-62e0b3d066d74370aedcdaa3158493f7
ACR-24a3c512009d462caad60d66de61cc40
 */
package org.sonarsource.sonarlint.core.serverapi.plugins;

public class ServerPlugin {
  private final String key;
  private final String hash;
  private final String filename;
  private final boolean sonarLintSupported;

  public ServerPlugin(String key, String hash, String filename, boolean sonarLintSupported) {
    this.key = key;
    this.hash = hash;
    this.filename = filename;
    this.sonarLintSupported = sonarLintSupported;
  }

  public String getKey() {
    return key;
  }

  public String getHash() {
    return hash;
  }

  public String getFilename() {
    return filename;
  }

  public boolean isSonarLintSupported() {
    return sonarLintSupported;
  }
}
