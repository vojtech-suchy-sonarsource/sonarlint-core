/*
ACR-fc3985342570430ca9ae03e390f2a42b
ACR-3b5029179a2545c88f06bde660144069
ACR-fe1ffd252c16493e8dcea9b8dfc96f51
ACR-91c2bc259e8149dda43e796f9eaa56ab
ACR-1fe9195f342947f59d217ad12efb9316
ACR-42d4e3a5785b497cbed6c2acc3e00e93
ACR-e40e10e8dd7d46688263a25b8a40cd90
ACR-ef751987497a4128baaca965113f93c6
ACR-a9e3b76b2b8a4e4e92300b6dace648ec
ACR-654e77932831485f8d7118e425128387
ACR-953f809ca57e4f6cb1f180d9d3b35444
ACR-34c9d40fd80c472a93777bb27e1ad390
ACR-0799dd3419044d4aba05207184e7667e
ACR-50c892c81b924f61a9ea5544aac802cd
ACR-22ab3ed8fb674636b74c673c43905b1a
ACR-c54843f23f1348498175b3f86faa4385
ACR-436eb14eff3c4219bf62a4c14048103c
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
