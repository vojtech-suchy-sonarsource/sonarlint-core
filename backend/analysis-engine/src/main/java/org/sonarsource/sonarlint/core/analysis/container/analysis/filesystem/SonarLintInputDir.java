/*
ACR-7f73668291174591a2b2561cbecc2a08
ACR-e9f910100ac8400fbeb8b276d46b299e
ACR-0ceff3eff768402da1072495b38fa429
ACR-a798ee9a81954abcb609cdf6f0d419f4
ACR-7e92cfbc43204bab9c93b2254be4f918
ACR-1a04e41ebca443a78a34c00f6d7a7a50
ACR-1e653ec6f12a4ba79f28c8e924728577
ACR-23639d9b94f3462282073f3ae29ca531
ACR-1651f51af6444e50a20b78cb8981a8ca
ACR-c1a36dbbd7d54350b3e6ab8ca88abbe3
ACR-10d78d208e644fb995cf89bdcc3d9fe8
ACR-9337390a9642419fa6475c4d9b95ec33
ACR-44de9a26a7fd4c23a4d78c636d5556a3
ACR-1431b444ebbe494e830de082dbe468a8
ACR-a5c572578f534da18fd003b8281481b0
ACR-085cb1896d524804a196e27ab5a73ce5
ACR-caae971ed18145608d98d8bfbbf6bf3b
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.utils.PathUtils;

/*ACR-122ed7c581bd4a6580b479fc97899063
ACR-7f8c5d2e9af54aa8ad3e0a8f648bd927
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
