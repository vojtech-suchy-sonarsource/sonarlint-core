/*
ACR-a3c676f9bb3c4de498f9dd2f115183a7
ACR-f14f4e53f310465fbf3bf7a958fcffc6
ACR-9cb11fc33245469a936ed9f7dab4ea7a
ACR-3669c98802ae425299923c65d9ce4d4b
ACR-a51e398d03494bf48c93a7167eeca853
ACR-4499691eddba4823ad59b70831ab4194
ACR-25b31cf9ffa941b2b09b5a2c399303c3
ACR-8c8c87551d3242bb8903569aa839ff43
ACR-63ffcb917123460982cb8b2e746f0125
ACR-afdbcf9917314e3d80bccc3d1c8e2182
ACR-d6f30498d6194ffe9540569f4700acfc
ACR-2a8165c64b124b479a5368f42323ee7a
ACR-daab20d943fb4c55a509cecc1786a796
ACR-2284c45654c84e7eb387c72bb1c320d9
ACR-c792dd739f1744ccb9a1feaef7419d69
ACR-c137f8e8eefd4091b2d3b125e5e1c886
ACR-0b4c247be8544bb595922cd686129c9e
 */
package mediumtest.sloop;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class JreLocator {
  private static final String JRE_WINDOWS_PATH = "target/jre-windows/";
  private static final String JRE_LINUX_PATH = "target/jre-linux/";

  public static Path getWindowsJrePath() {
    return getJrePath(JRE_WINDOWS_PATH);
  }

  public static Path getLinuxJrePath() {
    return getJrePath(JRE_LINUX_PATH);
  }

  @NotNull
  private static Path getJrePath(String jreLinuxPath) {
    var jreDir = Paths.get(jreLinuxPath).toAbsolutePath().normalize().toFile();
    return Arrays.stream(requireNonNull(jreDir.listFiles())).findFirst().get().toPath();
  }
}
