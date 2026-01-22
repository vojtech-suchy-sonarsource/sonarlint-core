/*
ACR-b3f22739d6364f148a6a136fd8cf615e
ACR-cbb849f8f5234dad8931d3ea7e9eb831
ACR-79a68ea69c5e49268ffaa4d13b7a68c3
ACR-ce28837cde584f5ea260b90bb681313b
ACR-a1ef9a73ccdd48dcb6627fbf2c2833cf
ACR-89d750769f8540e491c09efc5ac10d5e
ACR-96b04a65c61441f79bb57252d59a4e3f
ACR-b4b2e0951e7d43028da1dc3f8316583e
ACR-b9b09718f5e94879992ab906c9b9b1a9
ACR-fcd5db4460ad4403a8c02ae2df75990f
ACR-dc2037583c1d4124b15a213dc0c2a955
ACR-5a2b27977bdf447dbeb288b757376beb
ACR-d7a15f6640b74c24b6a68dbc398f4604
ACR-7bf42b96226246a591cd57b4f5343ee6
ACR-a60d6a31df404109ae8a41d1f0c73d43
ACR-3d01b1407c8f4c789223127c4ce15c76
ACR-7b5e819cb4ad41d380dc1dfd0eca2ee3
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.utils.PathUtils;

/*ACR-0dc55a5c5e1e4a94bb4ff8f5bacf56e0
ACR-3469a3038e07469093997be7dba96864
 */
public class SonarLintInputDir implements InputDir {

  private final Path path;

  public SonarLintInputDir(Path path) {
    this.path = path;
  }

  @Override
  public String relativePath() {
    return absolutePath();
  }

  @Override
  public String absolutePath() {
    return PathUtils.sanitize(path().toString());
  }

  @Override
  public File file() {
    return path().toFile();
  }

  @Override
  public Path path() {
    return path;
  }

  @Override
  public String key() {
    return absolutePath();
  }

  @Override
  public URI uri() {
    return path.toUri();
  }

  @Override
  public boolean isFile() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof SonarLintInputDir dir)) {
      return false;
    }

    return path().equals(dir.path());
  }

  @Override
  public int hashCode() {
    return path().hashCode();
  }

  @Override
  public String toString() {
    return "[path=" + path() + "]";
  }

}
