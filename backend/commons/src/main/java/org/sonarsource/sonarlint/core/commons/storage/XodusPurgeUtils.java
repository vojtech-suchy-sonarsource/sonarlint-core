/*
ACR-f94c47dee0d4483086b2c04d3dccffcc
ACR-f096ae65a2eb40738f178aa89a2471bc
ACR-bb9ac2141942447093636818e3c72e83
ACR-d1ca4edc638b497488f512b00d0588db
ACR-85aba0f0dda74d92abbbf615c2f1dc9c
ACR-ebd68d168b8540bd886592d0afd9f5cf
ACR-d673e65393404aec9eca71dca556330f
ACR-c6b503b8fe624071af52c571d9bac4a4
ACR-4791a50fdb134e5eb3a21a7b67b2898a
ACR-00db6ee1557b4225bd1c34cde811f41a
ACR-e37179ce919e4552a7b1e2fcd463dd0a
ACR-bf751aa5ef4f4e3990f2c87c65e83044
ACR-194fd6b4987c4134b4b29faad5f95e12
ACR-4c69e7467a69485dbf77afd27244534e
ACR-ab49740b7c464cad9f5c7cff1b408061
ACR-81649c461b9a43e48153d9c5dcb67fb9
ACR-4d0bd121ae7e4301a89290b8a01b04c9
 */
package org.sonarsource.sonarlint.core.commons.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class XodusPurgeUtils {

  private XodusPurgeUtils() {
    //ACR-fd18365c39b34a83aae613c01a0f4e36
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
