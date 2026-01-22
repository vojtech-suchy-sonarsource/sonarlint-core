/*
ACR-5e88f8990dbd41d1900235aa3a8b342f
ACR-faf662b1c65e49cb82209caa250fac02
ACR-d78492bec44e4df1ad4f347848b8ca59
ACR-f092be4520a84a2e8b564a056ed945ed
ACR-6110d134a1ed407a99f5b43c785372f9
ACR-ed2d841e8b6b4b53a2fd1b999f6564ce
ACR-01ef56bfc8a14a5288c39af6222be43b
ACR-82b8ef856184475c97c04c17f9a182e4
ACR-465987df82c849068e8c927ce542105b
ACR-87292d7d84104b70b200bbe875f5e544
ACR-bb0b1a9061544592af2d70e57ff9ca59
ACR-baf0836138c345deaa6e3e8886ef864d
ACR-9e0e713ba2244c19b4f9d796858a47a3
ACR-1d27ffe0ad67443fa9117e1404e5a343
ACR-9982ee9b1dc54e138d12ca4fd7236fa9
ACR-9164d3b292a54dc9aee5f42ddbf218a0
ACR-a4a81d2b49014b0e90f1f1c91036d66d
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.LogOutput.Level;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultTempFolderTests {

  @RegisterExtension
  SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void createTempFolderAndFile(@TempDir File rootTempFolder) throws Exception {
    var underTest = new DefaultTempFolder(rootTempFolder);
    var dir = underTest.newDir();
    assertThat(dir).exists().isDirectory();
    var file = underTest.newFile();
    assertThat(file).exists().isFile();

    underTest.clean();
    assertThat(rootTempFolder).doesNotExist();
  }

  @Test
  void createTempFolderWithName(@TempDir File rootTempFolder) throws Exception {
    var underTest = new DefaultTempFolder(rootTempFolder);
    var dir = underTest.newDir("sample");
    assertThat(dir).exists().isDirectory();
    assertThat(new File(rootTempFolder, "sample")).isEqualTo(dir);

    underTest.clean();
    assertThat(rootTempFolder).doesNotExist();
  }

  @Test
  void newDir_throws_ISE_if_name_is_not_valid(@TempDir File rootTempFolder) throws Exception {
    var underTest = new DefaultTempFolder(rootTempFolder);
    var tooLong = new StringBuilder("tooooolong");
    for (var i = 0; i < 50; i++) {
      tooLong.append("tooooolong");
    }

    var thrown = assertThrows(IllegalStateException.class, () -> underTest.newDir(tooLong.toString()));
    assertThat(thrown).hasMessageStartingWith("Failed to create temp directory");
  }

  @Test
  void newFile_throws_ISE_if_name_is_not_valid(@TempDir File rootTempFolder) throws Exception {
    var underTest = new DefaultTempFolder(rootTempFolder);
    var tooLong = new StringBuilder("tooooolong");
    for (var i = 0; i < 50; i++) {
      tooLong.append("tooooolong");
    }

    var thrown = assertThrows(IllegalStateException.class, () -> underTest.newFile(tooLong.toString(), ".txt"));
    assertThat(thrown).hasMessage("Failed to create temp file");
  }

  @Test
  void clean_deletes_non_empty_directory(@TempDir File dir) throws Exception {
    FileUtils.touch(new File(dir, "foo.txt"));

    var underTest = new DefaultTempFolder(dir);
    underTest.clean();

    assertThat(dir).doesNotExist();
  }

  @Test
  void clean_does_not_fail_if_directory_has_already_been_deleted(@TempDir File dir) throws Exception {
    var underTest = new DefaultTempFolder(dir);
    underTest.clean();
    assertThat(dir).doesNotExist();

    //ACR-b30ba9b3f6264b88863aade8ab2813e6
    underTest.clean();

    assertThat(logTester.logs(Level.ERROR)).isEmpty();
  }
}
