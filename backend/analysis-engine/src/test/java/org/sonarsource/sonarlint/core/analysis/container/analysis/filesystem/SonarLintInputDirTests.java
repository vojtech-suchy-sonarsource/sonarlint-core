/*
ACR-d794c675afd44b4fa3b49ea5cbe5bda5
ACR-fd5804aa43bc498886e99777315749d2
ACR-7e27e14c91484db6bc6adb0cb2c05431
ACR-23ed04b7504349de924fb6666c6bfede
ACR-5c2c290f9cbc46739c83aa3ce37931d5
ACR-bb6c6bd6e8f04b9990df427043e44e6d
ACR-7433616e20c44ce3a4a14e50b3233c94
ACR-5c6cdc20b82445b2b9d08ab932fb466e
ACR-cb5d8c95e1804cd8ada0c00f8fb046b1
ACR-4a72c979127d4a5ea66ae79da550bf39
ACR-27f1748aee0d4c1f8fbdf7bf089c5b6a
ACR-0b4850c4e62e4d40b68bddc97a305942
ACR-a9381dd930884fd1978bcbf21bd4bc4b
ACR-0d94773dd3d04629ae3cc26e2219251c
ACR-94f142c1fb2541e396feb706f939597d
ACR-3e291048ef084163a4d2f6455f63d853
ACR-27ffe982c97b4d57a1f722fe9ab6ff8e
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.utils.PathUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SonarLintInputDirTests {
  private SonarLintInputDir inputDir;
  private Path path;

  @BeforeEach
  void setUp() {
    path = Paths.get("file1").toAbsolutePath();
    inputDir = new SonarLintInputDir(path);
  }

  @Test
  void testInputDir() {
    assertThat(inputDir.absolutePath()).isEqualTo(PathUtils.canonicalPath(path.toFile()));
    assertThat(inputDir.file()).isEqualTo(path.toFile());
    assertThat(inputDir.key()).isEqualTo(PathUtils.canonicalPath(path.toFile()));
    assertThat(inputDir.isFile()).isFalse();
    assertThat(inputDir.path()).isEqualTo(path);
    assertThat(inputDir.relativePath()).isEqualTo(inputDir.absolutePath());
    assertThat(inputDir)
      .hasToString("[path=" + path + "]")
      .isNotEqualTo(mock(SonarLintInputDir.class))
      .isEqualTo(inputDir);
  }

}
