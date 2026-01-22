/*
ACR-4e813f3888a34f26af8ed6ac2fe86587
ACR-4461d9ac64ce40b9963579ce01b230ac
ACR-8228d993bcb642e4b649b41a2cf0ba92
ACR-0c1585e7b45a496abd7483bf4fc7774d
ACR-0ad04012df5542fc8d989176ed0a6a7b
ACR-5042133101e04aae83b21ce6f1b0c03a
ACR-209125d3327a4b1a950978fe5229adf1
ACR-e116c9de28954a8d96af301e6bab6d9a
ACR-321b46f24d00428b908aea2dd37d66ce
ACR-249b29769fff43f8b02421b444522aea
ACR-5587bc5a51354072a661b107108fdc75
ACR-d126faecfe154e89a9ec717f14366203
ACR-21e3d50a6eb345d79a736c0e30e5e84a
ACR-3281d1b832534aaaa189b939e11b9ce3
ACR-2a692687e8e3400cbdd16b8631344a57
ACR-9ae426b5b8c140878061012b65af4b63
ACR-2705f85dc88b4935bece0dc9169fd578
 */
package mediumtest.sloop;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

public class SloopDistLocator {

  private static final String SLOOP_DIST_PATH = "target";
  private static final String WINDOWS_DIST_REGEXP = "^sonarlint-backend-cli-([0-9.]+)(-SNAPSHOT)*-windows_x64.zip$";
  private static final String LINUX_64_DIST_REGEXP = "^sonarlint-backend-cli-([0-9.]+)(-SNAPSHOT)*-linux_x64.tar.gz$";

  public static Path getLinux64DistPath() {
    return getSloopDistPath(LINUX_64_DIST_REGEXP);
  }

  public static Path getWindowsDistPath() {
    return getSloopDistPath(WINDOWS_DIST_REGEXP);
  }

  private static Path getSloopDistPath(String regexp) {
    var sloopDistDir = Paths.get(SLOOP_DIST_PATH).toAbsolutePath().normalize().toFile();
    return FileUtils.listFiles(sloopDistDir, new RegexFileFilter(regexp), FalseFileFilter.FALSE).iterator().next().toPath();
  }
}
