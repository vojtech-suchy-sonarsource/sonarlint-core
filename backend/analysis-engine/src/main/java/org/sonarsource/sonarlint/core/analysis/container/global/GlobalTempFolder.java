/*
ACR-2a9619b41a014cb7b8194c5c2f200d05
ACR-605b3f0ed9ea46c4a38f5d5352c62953
ACR-916298550ec640ddbc3ee3ff70888f32
ACR-1004cf14b20a467aa6d69c19a9cbeaff
ACR-46f5689e56744e56bd18018ff272a564
ACR-0686f9d8c6b64c33858e057110ea313a
ACR-31a1ac0c9c9344c283cdf819f9336bc9
ACR-405e7f6c12614bdeb0abb31599cbad94
ACR-c8866032a23b4d74b3afc39aa5f3e5f3
ACR-ca994ffaea2d4bdeaa8c6d3ce14f0899
ACR-05ef196eb74442c496a0b2f54a11f8d0
ACR-3887a264ebdc477482a86d302e5b28cc
ACR-d7092a63ff3449cfae572842944294b5
ACR-1f5333c9b2c34671923840e8c51e5212
ACR-58f7c94529e5403f85aec01e2c8b3f02
ACR-69294eace2e44135baa7294eb626375a
ACR-46df7e3ffd82480ba8525421d9585e2c
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.io.File;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultTempFolder;

public class GlobalTempFolder extends DefaultTempFolder {

  public GlobalTempFolder(File tempDir, boolean deleteOnExit) {
    super(tempDir, deleteOnExit);
  }

}
