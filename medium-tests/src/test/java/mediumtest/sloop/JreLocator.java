/*
ACR-17c848353165426bbe81ac8ad93fbd1f
ACR-24acdda5a3214cc58fcac4434b26e676
ACR-978fd58c6b8f4c47a47902aa5d0207a9
ACR-db528561248d4533bc9db52908f0f46d
ACR-653f33ff704741fcaa7a12c4ca10df2d
ACR-e53c177970fc486ba00671192ed59436
ACR-2612c1250eba42b1b46f71258a1eafbb
ACR-c19c7da48ad24b388a80153360a7c94c
ACR-09fa9dbfac69426fa9da3178c1041465
ACR-89c18c407b7f4d9d97ff5015194218a3
ACR-4f8526e3496f4d12890f3ed59b28ea0d
ACR-4b13ca5e4a8944109ce328d6a4984645
ACR-72d402eff25847f397cfc883e2515ea1
ACR-d9998cfed7994fa9b99321ec469f8dd7
ACR-b9bcf6deb2434b9dbc0a37162bd4f883
ACR-8d84b5c81fbb4ea4bbe441006e352146
ACR-e6f438e1776a44db8451cb84d4c36979
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
