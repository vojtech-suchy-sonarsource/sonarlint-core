/*
ACR-d2e43896cf92441f99e5514a4c22b18b
ACR-6e6795c785404214994eedc377f4d184
ACR-9b53dc6c72b6497484ca4c14db27c0a9
ACR-aa1b8c1d19724033bbb3e580540cda2c
ACR-4f84ec75456c4049b4e9ecc07539f81c
ACR-ce98bef9e022464a8eb7d97052213a1b
ACR-2a20d82465a94ea79616388235be575d
ACR-3290f61dbdf64f00b37fa8ec39bd2c1e
ACR-0c1e1a581bc74055b95bba94cfd099c5
ACR-aff21b392c25469bb1f4da3aa536c66b
ACR-103d71904b794654817ebfd5b0d33a22
ACR-4802a3a417a64c98968d97807d8c6cd9
ACR-7276fffc185147feb826bc30e879d1e7
ACR-92116db27c2d4728aeb92d064e3ce284
ACR-460cedfd75964dba8c3d5e6e318a9bd4
ACR-b121c3a0b96241ccb55ccf77b8806afa
ACR-59b47b4b41f24e5aa79be5befbccc65c
 */
package mediumtest.sloop;

import java.nio.file.Path;
import org.apache.commons.lang3.SystemUtils;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;

public class UnArchiveUtils {

  public static void unarchiveDistribution(String inputFilePath, Path destionationPath, FileSelector[] fileSelectors) {
    var unArchiver = SystemUtils.IS_OS_WINDOWS ? new ZipUnArchiver() : new TarGZipUnArchiver();
    var outputDirectory = destionationPath.toFile();
    outputDirectory.mkdirs();
    var inputFile = Path.of(inputFilePath).toFile();
    unArchiver.setSourceFile(inputFile);
    unArchiver.setFileSelectors(fileSelectors);
    unArchiver.setDestDirectory(outputDirectory);
    unArchiver.extract();
  }

  public static void unarchiveDistribution(String inputFilePath, Path destionationPath) {
    unarchiveDistribution(inputFilePath, destionationPath, new FileSelector[] {});
  }
}
