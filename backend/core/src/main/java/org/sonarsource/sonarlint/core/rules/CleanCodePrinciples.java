/*
ACR-567c0cb82b4b4bb3b9f3a7769813e660
ACR-1ace8ec55ba34e64a84c290c683df0dc
ACR-73be67b6574e46268e92fe5663d051a2
ACR-9c97fdcbfbc949ba8a505b761e97b5db
ACR-7ab237d571214e3d9146bcfb76062cc3
ACR-31e726a7336b4388845a437ef5e3bd03
ACR-be18368528c44a9398a14b46d59084f6
ACR-4cbc0d5f3a3b4b8cb71fbeac0093fca1
ACR-dcf857b8c2374ad0bbcd7832468f5893
ACR-0a608dd849224f9bbfcddbc739ed3f19
ACR-b94431e644334061acce829ec4230fd3
ACR-d108355ddd42402ebe2143e8a26d9ffd
ACR-c120c6a3b31c41ff9f9b00dee3359cb6
ACR-9ce034facb15416aad5426f3e90d4873
ACR-8037d496945047de8824e8fb94cf053d
ACR-c814461d81d044e3a8e7c1d738c823e8
ACR-8389a768137b4b72aef1bf08c8fd3627
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
    //ACR-e35c9be9662e44a7947f9bbffe34af45
  }
}
