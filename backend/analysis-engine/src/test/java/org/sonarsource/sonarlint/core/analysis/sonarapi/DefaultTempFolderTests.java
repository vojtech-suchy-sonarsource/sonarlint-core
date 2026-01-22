/*
ACR-d1ad394677c44986b4b4b0622b0b64c2
ACR-89cbd8c8c3404fce8ae4026b4ba524fa
ACR-a53a5f057f904dba866b329b423be643
ACR-020e6a4cac694ed0ae0e2532292ff693
ACR-db7ee52800c446708d92e8a4374b7c06
ACR-a8de115392f74256a9d5b26e5b7c3aa6
ACR-65263e0ee9ac49048e0bfe6cef27a476
ACR-5aa97ea94f7245cfb5599b8b63e0cd7d
ACR-ab50dcddce75452db6a3cd063a456d72
ACR-fe3235e2ea4e4c25a4ac771980af30cd
ACR-97f19abdc15048df9e5d65e1a9e9cf68
ACR-ba9d9063056b449382e5a63a12373b38
ACR-feb7b1b3ed454698a945b722b2e82201
ACR-52fad1f5930b4abe9383471164e41825
ACR-62ae77d55657464991740e20b6594ff3
ACR-53494e3b10304f26bb918d23650ac2a3
ACR-8ac230620792410597d763c3e760635c
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

    //ACR-dcfb3d334bf14122b19c4e371894559b
    underTest.clean();

    assertThat(logTester.logs(Level.ERROR)).isEmpty();
  }
}
