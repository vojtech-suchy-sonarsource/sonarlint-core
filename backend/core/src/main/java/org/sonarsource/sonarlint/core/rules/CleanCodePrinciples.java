/*
ACR-300c6bb80a4640b7926465bacaf7d5e5
ACR-acef27d7bba143f9abcbd4662b28724e
ACR-e3734f8df6144af1b44916efbcd102cd
ACR-036d8e2437904e32a06b39964a13c8bf
ACR-479edc7bb3584a73b4c7ce1447f916b0
ACR-b10aabd39d08471dbaf10087db5eb6a4
ACR-3ab8775545fe410f9f4a7ce9cbb66739
ACR-86bcc54e623b4382a6ab5229722d8686
ACR-5d1e156b0479435da6f8189337bf3f5d
ACR-40883919fd274f2f8a45354256faa098
ACR-c234b7fef4f144a6b771d7b208370cef
ACR-816c02bda46e4f6293021b634e3d195c
ACR-81efdb1057f34acba333ba5708916e57
ACR-d60a7f2e75ac456aa39c74d3f29cffa5
ACR-c7fc13526cc84a14ba659e028cb91ff6
ACR-83c77f8424ba4326a39822bc64365a50
ACR-05fca88a2e2c4c7ab54535e639efd172
 */
package org.sonarsource.sonarlint.core.rules;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.annotation.CheckForNull;
import org.apache.commons.io.IOUtils;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

class CleanCodePrinciples {

  @CheckForNull
  public static String getContent(String key) {
    var fileStream = CleanCodePrinciples.class.getResourceAsStream("/clean-code-principles/" + key + ".html");
    if (fileStream == null) {
      SonarLintLogger.get().info("Unsupported clean code principle key: " + key);
      return null;
    }
    try {
      return IOUtils.toString(fileStream, StandardCharsets.UTF_8).trim().replaceAll("\\r\\n?", "\n");
    } catch (IOException e) {
      SonarLintLogger.get().error("Could not read content for clean code principle key: " + key, e);
      return null;
    }
  }

  private CleanCodePrinciples() {
    //ACR-debddb757150402fbf7cf4fccffcee1a
  }
}
