/*
ACR-324e01bd59e049dca4d03293f1906528
ACR-6463a8e1831b47e6839459951ade89e9
ACR-e9d0c54eaf2a467e8d6d8bcd790e2335
ACR-bdfa8ede6546445c83b50f565bc98209
ACR-1590622334a1434d8cdaf2d6158b345a
ACR-bdf6ba8cd1b34d479cd3a2747610329a
ACR-9aa3f6b2d656469e8fac54ebcae820d4
ACR-8ef9e3a38cd44e21aaed1e34840b8ba1
ACR-9a9ef8917c71449e855657fa6715d45f
ACR-83795f419fa246c9ad3fa41347767843
ACR-73c3d99b6098406b80ca8bae8b3c172c
ACR-42d859cd4b4e4f5daca3fb5f8b02060f
ACR-b2c4f94f9f0949f1824094ae4a478610
ACR-a40811665af747b081ed83750c846057
ACR-93c578827d70435a8a23a400473cd04c
ACR-7308097bb69446dcbbcc820c847a44d3
ACR-c5f3bc976eda4f5ca9fcabaa13a7e1a5
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

public class TarGzUtils {

  private TarGzUtils() {

  }

  public static void extractTarGz(Path tarGzFile, Path destinationDir) throws IOException {
    try (var fi = Files.newInputStream(tarGzFile);
      var bi = new BufferedInputStream(fi);
      var gzi = new GzipCompressorInputStream(bi);
      var o = new TarArchiveInputStream(gzi)) {
      ArchiveEntry entry = null;
      while ((entry = o.getNextEntry()) != null) {
        if (!o.canReadEntryData(entry)) {
          throw new IllegalStateException("Unable to read entry data from " + tarGzFile);
        }
        Path f = fileName(destinationDir, entry);
        if (entry.isDirectory()) {
          Files.createDirectories(f);
        } else {
          Path parent = f.getParent();
          Files.createDirectories(parent);

          try (var os = Files.newOutputStream(f)) {
            IOUtils.copy(o, os);
          }
        }
      }
    }
  }

  private static Path fileName(Path destinationDir, ArchiveEntry zipEntry) throws IOException {
    var destFile = destinationDir.resolve(zipEntry.getName());

    if (!destFile.normalize().startsWith(destinationDir)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }

}
