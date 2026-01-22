/*
ACR-c456f4b7066d441e8f5457a8a09ab927
ACR-98ff18a2e4784f1c9f3ca7e8a31e3377
ACR-1ff8207f9da74f37be5d767eaa7e43f3
ACR-a23a3424a34e4a618f456c84dbd7e0f5
ACR-a45519e01e754513939e4d5353403e7c
ACR-686760e0b6dc4fef819d6c912ed77604
ACR-fc171cab68434b33bd43d209ef5bdbdf
ACR-088560d813554f618c2323fee9e49fef
ACR-aa5644226a8a4b3da794b119fa9ccaa7
ACR-fd2e3d8d2fbf46ed9455e3e1450de8ae
ACR-dc44d8ccfab543979796b5b50e207ad9
ACR-64aabaf1a4a14bdb9d3b8fe9a0ad57df
ACR-dc4b9c24389d4eaea67d6746b7612321
ACR-c871609f3d154036be0a5985fd3cf95d
ACR-64edf5ac96c440e7871f17c8f735b04f
ACR-781f6f8750c74646b0b844a7ea2d802f
ACR-47d02dccbd6e45049ea13617f3660500
 */
package org.sonarsource.sonarlint.core.commons.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class XodusPurgeUtils {

  private XodusPurgeUtils() {
    //ACR-3398968a423f4dc2b0b114713649126c
  }

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public static void deleteInFolderWithPattern(Path folder, String pattern) {
    if (Files.exists(folder)) {
      try (var stream = Files.newDirectoryStream(folder, pattern)) {
        for (var path : stream) {
          FileUtils.deleteQuietly(path.toFile());
        }
      } catch (Exception e) {
        LOG.error("Unable to remove files in {} for pattern {}", folder, pattern, e);
      }
    }
  }

}
