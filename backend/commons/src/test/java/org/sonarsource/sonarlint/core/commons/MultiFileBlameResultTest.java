/*
ACR-b2004d6ca60a40009789e3d6fd3b6de9
ACR-bfa5c40e14c945a2a2f5bd4816891495
ACR-4470782de5274198be5a94da602360d5
ACR-46d0c0442b304805ba42cc1d00d4b6d7
ACR-3a2c8760cd1241269b8e1153c68bfdb3
ACR-10a707d21a994361ac98c5a208df1b48
ACR-f6ffb81f2c95416da0c913751e208844
ACR-650242ef4a764f9ab52ba1e509b28f4a
ACR-5dcd8e3b81ff449e9aee102cc2bfc121
ACR-340df4b0c3cb43b79957ab5d7ddee00d
ACR-eb93066549c644ee8d538cdb72634ade
ACR-1fb2ade9ee2c419693dd508183c32737
ACR-b124cd15060e4bf8b0d1de60b084f46f
ACR-a218db2eea5747e88b24b49c0428fe00
ACR-4c3ec848cf3e41f58bd71fdb024fa4b5
ACR-40d81ec3233246bfb82e48d3bdc095f9
ACR-7129203c078646d8b83c8b81707acac7
 */
package org.sonarsource.sonarlint.core.commons;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jgit.util.FileUtils.RECURSIVE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.appendFile;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.commit;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.commitAtDate;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.createFile;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.createRepository;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.modifyFile;
import static org.sonarsource.sonarlint.core.commons.util.git.GitService.blameWithGitFilesBlameLibrary;

class MultiFileBlameResultTest {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private Git git;
  @TempDir
  private Path projectDir;

  @BeforeEach
  public void prepare() throws IOException, GitAPIException {
    git = createRepository(projectDir);
  }

  @AfterEach
  public void cleanup() throws IOException {
    FileUtils.delete(projectDir.toFile(), RECURSIVE);
  }

  @Test
  void it_should_return_correct_latest_changed_date_for_file_lines() throws IOException, GitAPIException, InterruptedException {
    createFile(projectDir, "fileA", "line1", "line2", "line3");
    var c1 = commit(git, "fileA");

    //ACR-f3abf8f83c2e468a876983c7b793568e
    TimeUnit.MILLISECONDS.sleep(10);
    appendFile(projectDir.resolve("fileA"), "new line 4");
    var c2 = commit(git, "fileA");

    //ACR-a8e2b3124d044d9a9de8f0a5c6419e40
    TimeUnit.MILLISECONDS.sleep(10);
    createFile(projectDir, "fileB", "line1", "line2", "line3");
    var c3 = commit(git, "fileB");

    createFile(projectDir, "fileC", "line1", "line2", "line3");
    commit(git, "fileC");

    var results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of("fileA"), Path.of("fileB")), null);

    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1, 2))).isPresent().contains(c1);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(2, 3))).isPresent().contains(c1);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(3, 4))).isPresent().contains(c2);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileB"), List.of(1, 2))).isPresent().contains(c3);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileC"), List.of(1, 2))).isEmpty();
  }

  @Test
  void it_should_handle_all_line_modified() throws IOException, GitAPIException {
    createFile(projectDir, "fileA", "line1", "line2", "line3");
    var c1 = commit(git, "fileA");

    var results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of("fileA")), null);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1, 2, 3))).isPresent().contains(c1);

    modifyFile(projectDir.resolve("fileA"), "new line1", "new line2", "new line3");

    results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of("fileA")), null);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1, 2, 3))).isEmpty();
  }

  @Test
  void it_should_return_latest_change_date() throws IOException, GitAPIException {
    createFile(projectDir, "fileA", "line1", "line2", "line3");
    var now = Instant.now();
    var c1 = commitAtDate(git, now.minus(1, ChronoUnit.DAYS), "fileA");

    var results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of("fileA")), null);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1, 2, 3))).isPresent().contains(c1);
    modifyFile(projectDir.resolve("fileA"), "line1", "line2", "new line3");
    commitAtDate(git, now, "fileA");

    results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of("fileA")), null);
    var result = results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1, 2, 3));

    assertThat(result).isPresent();
    assertThat(ChronoUnit.MINUTES.between(result.get(), now)).isZero();
  }

  @Test
  void it_should_handle_end_of_line_modified() throws IOException, GitAPIException {
    createFile(projectDir, "fileA", "line1", "line2");
    var c1 = commit(git, "fileA");

    var results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of("fileA")), null);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1, 2))).isPresent().contains(c1);

    appendFile(projectDir.resolve("fileA"), "new line3", "new line4");

    results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of("fileA")), null);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1, 2, 3))).isEmpty();
  }

  @Test
  void it_should_handle_dodgy_input() throws IOException, GitAPIException {
    createFile(projectDir, "fileA", "line1", "line2", "line3");
    var c1 = commit(git, "fileA");

    var results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of("fileA"), Path.of("fileB")), null);

    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"),
      IntStream.rangeClosed(1, 100).boxed().toList())).isPresent().contains(c1);
    assertThat(results.getLatestChangeDateForLinesInFile(Path.of("fileA"),
      IntStream.rangeClosed(100, 1000).boxed().toList())).isEmpty();
  }

  @Test
  void it_should_raise_exception_if_wrong_line_numbering_provided() throws IOException, GitAPIException {
    createFile(projectDir, "fileA", "line1", "line2", "line3");
    commit(git, "fileA");

    var fileA = Path.of("fileA");
    var results = blameWithGitFilesBlameLibrary(projectDir, Set.of(fileA), null);
    var invalidLineNumbers = List.of(0, 1, 2);
    assertThrows(IllegalArgumentException.class, () -> results.getLatestChangeDateForLinesInFile(fileA, invalidLineNumbers));
  }

  @Test
  void it_should_handle_files_within_inner_dir() throws IOException, GitAPIException {
    var deepFilePath = Path.of("innerDir").resolve("fileA").toString();
    createFile(projectDir, deepFilePath, "line1", "line2", "line3");
    var c1 = commit(git, deepFilePath);

    var results = blameWithGitFilesBlameLibrary(projectDir, Set.of(Path.of(deepFilePath)), null);
    assertThat(results.getLatestChangeDateForLinesInFile(
      Path.of(deepFilePath),
      IntStream.rangeClosed(1, 100).boxed().toList()))
      .isPresent().contains(c1);
  }
}
