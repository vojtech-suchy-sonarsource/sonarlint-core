/*
ACR-d681a27e7fd144a6ad5a9df6e4a60e7f
ACR-5531f2976d2c4a73a3d0640c105d0923
ACR-5252e8245c4545089336bf30ed8c6a83
ACR-e530e235ef4048a2b0f73e58da39f8a3
ACR-0ac7721652bf4c79aa0177cc8327ffe0
ACR-feac3077cc0b4d7e9a2c2075fd33d5e3
ACR-25c42abc353048f796a6cdb0eaf75edb
ACR-379aaa2872cf4c1bba791b5c9c63c91e
ACR-df13eb2a89ac48589b0f61e664929410
ACR-d243483dd6c5408caedeeefb3596eca3
ACR-e16a136f57e245d88e27de7a191bb4b5
ACR-4a3cfbde358e4c588f719c548477cb14
ACR-3a2d67a9a1b7404f94c725a248e87a5f
ACR-d04f45b04cdb46efba8bd4210c3ceab0
ACR-0e70e72357974186b874ab8f46d8e711
ACR-0520aeb7b3fd460595df43a87029182a
ACR-3266d5e729954dc2a3f75f04ba144906
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
