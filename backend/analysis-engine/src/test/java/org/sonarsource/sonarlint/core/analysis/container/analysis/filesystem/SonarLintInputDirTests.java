/*
ACR-5de33cf9bfd14c418eeec6c8e66352ad
ACR-15d1c93d1aed4015a03141d137340572
ACR-796bc4a450bc4e648119cf4bbc228df9
ACR-787c8972ed7046198133ac101e9be665
ACR-13355656dd3d42e49cd103de318522b5
ACR-895d9b097a954e57bdd274416e35abd6
ACR-20d1d3b49ffc497583bb0b1d01ea8872
ACR-15619240f1bd4a96b8d65bd447415240
ACR-b6fa6222167641039f4a0e419323d0bc
ACR-38315aed28644d86b355e79502deaa24
ACR-85f1c3c0ba1d4182a5c76c02f44dd3e6
ACR-09356ac4ea864d3ebb17ec9c887e3530
ACR-74ffe34546e945d4b6bf798d559c487e
ACR-a2b6038ead3549df80ab6e7da405fdb9
ACR-4d43c4086aec426bb833c13a8009aaf9
ACR-7f843f3c0ae54450b0ab8ad889811a26
ACR-a094f1f642c74691b6bcaa122056e7fb
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
