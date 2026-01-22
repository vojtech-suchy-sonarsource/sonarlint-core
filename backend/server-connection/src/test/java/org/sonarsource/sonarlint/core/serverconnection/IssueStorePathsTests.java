/*
ACR-c779695f43a542448b8c69b12e4be444
ACR-4a38b89830524e0f89156a58aa17f2da
ACR-25f3beb98261442fa94f7701a39bb8d6
ACR-48346d1d55904c9383f575bc3bf4254a
ACR-878e3fd3cf1a41c7a462f360d55ab170
ACR-2a3ebfb253b14145b1da50069cdbbe84
ACR-6cc3f88bac3a449db8280fad9b5620c9
ACR-7fc7c1924e124a11a1d386fc6ac91685
ACR-1ddb9b9f4f0b472488423ff23d1d2505
ACR-fcec9bf73a474c0d8122fdb7b16674cb
ACR-7cbb2371dfc24cde8a4b44ada99390ea
ACR-490155fda7e3498caab7f7a6bac1340d
ACR-067b105be6714c4086482e736cf07d5a
ACR-4d1937a81fce492c9b11c7f574fb3f36
ACR-16816a9f4ba54c70a12d219e57a251fa
ACR-8cddbee757ad44cd8e01dfdbeb0ff23a
ACR-6d137fe6247e4ece950d2b8d36bc1c6a
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IssueStorePathsTests {

  @Test
  void local_path_to_sq_path_uses_both_prefixes() {
    var projectBinding = new ProjectBinding("project", "sq", "ide");
    var sqPath = IssueStorePaths.idePathToServerPath(projectBinding, Path.of("ide/project1/path1"));
    assertThat(sqPath).isEqualTo(Path.of("sq/project1/path1"));
  }

  @Test
  void local_path_to_fileKey() {
    var projectBinding = new ProjectBinding("projectKey", "project", "ide");
    var fileKey = IssueStorePaths.idePathToFileKey(projectBinding, Paths.get("ide/B/path1"));
    assertThat(fileKey).isEqualTo("projectKey:project/B/path1");
  }

  @Test
  void local_path_to_sq_path_returns_null_if_path_doesnt_match_prefix() {
    var projectBinding = new ProjectBinding("project", "sq", "ide");
    var sqPath = IssueStorePaths.idePathToServerPath(projectBinding, Path.of("unknown/project1/path1").normalize());
    assertThat(sqPath).isNull();
  }

  @Test
  void local_path_to_sq_path_returns_null_if_path_match_prefix_partially() {
    var projectBinding = new ProjectBinding("project", "sq", "src");
    var sqPath = IssueStorePaths.idePathToServerPath(projectBinding, Path.of("src2/project1/path1"));
    assertThat(sqPath).isNull();
  }

  @Test
  void local_path_to_sq_path_without_sq_prefix() {
    var projectBinding = new ProjectBinding("project", "", "ide");
    var sqPath = IssueStorePaths.idePathToServerPath(projectBinding, Path.of("ide/project1/path1"));
    assertThat(sqPath).isEqualTo(Path.of("project1/path1"));
  }

  @Test
  void local_path_to_sq_path_without_ide_prefix() {
    var projectBinding = new ProjectBinding("project", "sq", "");
    var sqPath = IssueStorePaths.idePathToServerPath(projectBinding, Path.of("ide/project1/path1"));
    assertThat(sqPath).isEqualTo(Path.of("sq/ide/project1/path1"));
  }

  @Test
  void local_path_to_fileKey_returns_null_if_path_doesnt_match_prefix() {
    var projectBinding = new ProjectBinding("project", "project", "ide");
    var fileKey = IssueStorePaths.idePathToFileKey(projectBinding, Path.of("unknown/B/path1"));
    assertThat(fileKey).isNull();
  }

}
