/*
ACR-c8c3884b6173401bb77faf716272204f
ACR-c76527e9f1254ed8b42228e1632a69b0
ACR-284681b389e643e5b0e453f191bfe039
ACR-467b7567c37343f192a02af07884d3ad
ACR-222539750d584342b7dfbdd5ef253c0d
ACR-ce436822c4544a96b80df9c5c38fe0ec
ACR-9d71e540b91847ad8b66bbcd830a4529
ACR-c329b9e27b6343439b6ec8ecac6eafa3
ACR-daabe504d1d141f7b448dfe47d6bc7a1
ACR-8f2a1eb194e74955aeaa8df4254ff98e
ACR-0cf74cd222a94e8d9767d06117186bf6
ACR-8926afe2a2f544f5a374fd4029f1875b
ACR-2cf5037c51c5436890fa1e56dc6d81f5
ACR-6062a284bd274ed7969d2b113227248f
ACR-fac43f2ec9aa466cb896951cf0022c4d
ACR-235c5265667b4af7831c5bc4af0f261b
ACR-5173d446c2004a48b45f662244592eca
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
