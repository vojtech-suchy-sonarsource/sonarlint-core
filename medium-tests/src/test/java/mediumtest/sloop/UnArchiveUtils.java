/*
ACR-e87132d4d30b4a858270dcaedfb16460
ACR-e9f016db54214f1b9d6e49ee64c8d9c7
ACR-ed340c34fbe1456d821f560c91988d99
ACR-bf2d428aeaa045e9a01f2dea0d57d538
ACR-5d894b1d0c584a8599839c4835b19746
ACR-20003fc2608f426b95fd82bd9feac98a
ACR-43562e761ebe4576a9b0606eebd958e8
ACR-fbd2adbb504b47b2a24bdd07d9aa9ff1
ACR-e3d85a404f9e49da83c2c1d875fb66f3
ACR-bf4372c09f1e44e0bc6f62ff5eebbc71
ACR-25dbc8fc961746d5b06a24ee7e52b87f
ACR-106d9a1fc5d8405d9bafe923a805bb14
ACR-b5bb13d86b7d4b3c8119692b90246669
ACR-ec0525dea5ea42beacbb4f23a5bee578
ACR-0f319d076f1d45e7b1391713f4b70b4a
ACR-4c4fc27a08a94ad0b25ff7bb655e186c
ACR-c9073ca32ec943ec92baff3586ba66a4
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
