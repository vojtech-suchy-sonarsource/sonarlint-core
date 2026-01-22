/*
ACR-8d956acb968a47929ffe1b15b4607d9d
ACR-89c50de9f4164a39b1913cb1d483bb11
ACR-1b1ffcefdbaf41a5a46a5cc698ab8208
ACR-11717af94d2b44b9b38ad116c8a46de2
ACR-ad959f85600d4b4ba29dc710af226b01
ACR-dc6046f2c1764d52adccb302eebe876f
ACR-efaa457812f1486fb253595c23696388
ACR-0e63c4555d8840eaad234e89e81fb6f9
ACR-119275f254f74df69280c2d06b6f2dad
ACR-e6696d2a56ce4b30986a1a53e2ff45d7
ACR-37bcc94d1a194132b6a15e9b267d28a8
ACR-f4f39e0eaf9847208b3146213901630f
ACR-13d7a75e6d12418eade3d6154eb7dccd
ACR-8daa9ef81d464c4bb04cc8f00da3b5e8
ACR-6f723cc19ff840529e69a3f9a7162549
ACR-d18d304b4eb54a40bc012c5fca083780
ACR-4ab23117cfec43378d94893e8ca2ccc8
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.LogTestStartAndEnd;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jgit.lib.Constants.GITIGNORE_FILENAME;
import static org.eclipse.jgit.util.FileUtils.RECURSIVE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.addFileToGitIgnoreAndCommit;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.commit;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.createFile;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.createRepository;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.modifyFile;
import static org.sonarsource.sonarlint.core.commons.util.git.GitService.blameWithGitFilesBlameLibrary;
import static org.sonarsource.sonarlint.core.commons.util.git.GitService.getVCSChangedFiles;

@ExtendWith(LogTestStartAndEnd.class)
class GitServiceTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static final NativeGitLocator REAL_NATIVE_GIT_LOCATOR = new NativeGitLocator();
  private static final GitService underTest = new GitService(REAL_NATIVE_GIT_LOCATOR);
  private static Path bareRepoPath;
  private static Path workingRepoPath;
  @TempDir
  private Path projectDirPath;
  private Git git;

  @BeforeAll
  static void beforeAll() throws GitAPIException, IOException {
    setUpBareRepo(Map.of(
      ".gitignore", "*.log\n*.tmp\n",
      "fileA", "lineA1\nlineA2\n",
      "fileB", "lineB1\nlineB2\n"
    ));
  }

  @AfterAll
  static void afterAll() {
    try {
      FileUtils.forceDelete(bareRepoPath.toFile());
      FileUtils.forceDelete(workingRepoPath.toFile());
    } catch (Exception ignored) {
      //ACR-3d869d84234245979c2981c3deba6909
    }
  }

  private static void setUpBareRepo(Map<String, String> filePathContentMap) throws IOException, GitAPIException {
    bareRepoPath = Files.createTempDirectory("bare-repo");
    workingRepoPath = Files.createTempDirectory("working-repo");
    //ACR-65395904c68a41b88ce496c902451b2a
    try (var ignored = Git.init().setBare(true).setDirectory(bareRepoPath.toFile()).call()) {
      //ACR-a1a4d4caace748658af11ef9ea767182
      try (var workingGit = Git.init().setDirectory(workingRepoPath.toFile()).call()) {
        //ACR-a15242f5932743e4acb16941d6d8ba85
        for (var filePath : filePathContentMap.keySet()) {
          var gitignoreFile = new File(workingRepoPath.toFile(), filePath);
          Files.writeString(gitignoreFile.toPath(), filePathContentMap.get(filePath));

          //ACR-a49359782a23422e9eabd8ddbc1ddf53
          workingGit.add().addFilepattern(filePath).call();
          workingGit.commit().setMessage("Add " + filePath).call();
        }

        //ACR-e6d41016514e4f8e894abde6b5bbe376
        workingGit.remoteAdd()
          .setName("origin")
          .setUri(new URIish(bareRepoPath.toUri().toString()))
          .call();
        workingGit.push().setRemote("origin").call();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @BeforeEach
  void prepare() throws Exception {
    git = createRepository(projectDirPath);
  }

  @AfterEach
  void cleanup() throws IOException {
    org.eclipse.jgit.util.FileUtils.delete(projectDirPath.toFile(), RECURSIVE);
  }

  @Test
  void it_should_blame_file() throws IOException, GitAPIException {
    createFile(projectDirPath, "fileA", "line1", "line2", "line3");
    var c1 = commit(git, "fileA");

    var sonarLintBlameResult = blameWithGitFilesBlameLibrary(projectDirPath, Set.of(Path.of("fileA")), null);
    assertThat(IntStream.of(1, 2, 3)
      .mapToObj(lineNumber -> sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(lineNumber))))
      .map(Optional::get)
      .allMatch(date -> date.equals(c1));
  }

  @Test
  void it_should_not_blame_new_file() throws IOException {
    createFile(projectDirPath, "fileA", "line1", "line2", "line3");

    var fileAPath = projectDirPath.resolve("fileA");
    var filePaths = Set.of(fileAPath);
    var fileUris = Set.of(fileAPath.toUri());
    var now = Instant.now();
    var blameResult = underTest.getBlameResult(projectDirPath, filePaths, fileUris, path -> "", now);
    assertThat(blameResult.getLatestChangeDateForLinesInFile(fileAPath, List.of(1))).isEmpty();
  }

  @Test
  void it_should_fallback_to_jgit_blame() throws IOException, GitAPIException {
    createFile(projectDirPath, "fileA", "line1", "line2", "line3");
    var c1 = commit(git, "fileA");

    var locator = mock(NativeGitLocator.class);
    when(locator.getNativeGitExecutable()).thenReturn(Optional.empty());
    var service = new GitService(locator);
    var sonarLintBlameResult = service.getBlameResult(projectDirPath, Set.of(Path.of("fileA")), Set.of(Path.of("fileA").toUri()), null, Instant.now());
    assertThat(IntStream.of(1, 2, 3)
      .mapToObj(lineNumber -> sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(lineNumber))))
      .map(Optional::get)
      .allMatch(date -> date.equals(c1));
  }

  @Test
  void it_should_blame_with_given_contents_within_inner_dir() throws IOException, GitAPIException {
    var deepFilePath = Path.of("innerDir").resolve("fileA").toString();
    createFile(projectDirPath, deepFilePath, "SonarQube", "SonarCloud", "SonarLint");
    var c1 = commit(git, deepFilePath);
    var content = String.join(System.lineSeparator(), "SonarQube", "Cloud", "SonarLint", "SonarSolution") + System.lineSeparator();

    UnaryOperator<String> fileContentProvider = path -> deepFilePath.equals(path) ? content : null;
    var sonarLintBlameResult = blameWithGitFilesBlameLibrary(projectDirPath, Set.of(Path.of(deepFilePath)), fileContentProvider);
    assertThat(IntStream.of(1, 2, 3, 4)
      .mapToObj(lineNumber -> sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of(deepFilePath), List.of(lineNumber))))
      .map(dateOpt -> dateOpt.orElse(null))
      .containsExactly(c1, null, c1, null);
  }

  @Test
  void it_should_blame_file_within_inner_dir() throws IOException, GitAPIException {
    var deepFilePath = Path.of("innerDir").resolve("fileA").toString();

    createFile(projectDirPath, deepFilePath, "line1", "line2", "line3");
    var c1 = commit(git, deepFilePath);

    var sonarLintBlameResult = blameWithGitFilesBlameLibrary(projectDirPath, Set.of(Path.of(deepFilePath)), null);
    var latestChangeDate = sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of(deepFilePath), List.of(1, 2, 3));
    assertThat(latestChangeDate).isPresent().contains(c1);
  }

  @Test
  void it_should_blame_project_files_when_project_base_is_sub_folder_of_git_repo() throws IOException, GitAPIException {
    projectDirPath = projectDirPath.resolve("subFolder");

    createFile(projectDirPath, "fileA", "line1", "line2", "line3");
    var c1 = commit(git, git.getRepository().getWorkTree().toPath().relativize(projectDirPath).resolve("fileA").toString());

    var sonarLintBlameResult = blameWithGitFilesBlameLibrary(projectDirPath, Set.of(Path.of("fileA")), null);
    assertThat(IntStream.of(1, 2, 3)
      .mapToObj(lineNumber -> sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(lineNumber))))
      .map(Optional::get)
      .allMatch(date -> date.equals(c1));
  }

  @Test
  void it_should_get_uncommitted_files_including_untracked_ones() throws GitAPIException, IOException {
    var committedFile = "committedFile";
    var committedAndModifiedFile = "committedAndModifiedFile";
    var uncommittedTrackedFile = "uncommittedTrackedFile";
    var uncommittedUntrackedFile = "uncommittedUntrackedFile";
    var committedFileUri = projectDirPath.resolve(committedFile).toUri();
    var committedAndModifiedFileUri = projectDirPath.resolve(committedAndModifiedFile).toUri();
    var uncommittedTrackedFileUri = projectDirPath.resolve(uncommittedTrackedFile).toUri();
    var uncommittedUntrackedFileUri = projectDirPath.resolve(uncommittedUntrackedFile).toUri();

    var folderFile = Path.of("folder").resolve("folderFile");
    var string = FilenameUtils.separatorsToUnix(folderFile.toString());
    createFile(projectDirPath, string, "line1", "line2", "line3");
    git.add().setUpdate(true).addFilepattern(string).call();

    createFile(projectDirPath, committedFile, "line1", "line2", "line3");
    commit(git, committedFile);

    createFile(projectDirPath, committedAndModifiedFile, "line1", "line2", "line3");
    commit(git, committedAndModifiedFile);
    modifyFile(projectDirPath.resolve(committedAndModifiedFile), "line1", "line2", "line3", "line4");

    createFile(projectDirPath, uncommittedTrackedFile, "line1", "line2", "line3");
    git.add().addFilepattern(uncommittedTrackedFile).call();

    createFile(projectDirPath, uncommittedUntrackedFile, "line1", "line2", "line3");

    var changedFiles = getVCSChangedFiles(projectDirPath);

    assertThat(changedFiles).hasSize(4)
      .doesNotContain(committedFileUri)
      .contains(committedAndModifiedFileUri)
      .contains(uncommittedTrackedFileUri)
      .contains(uncommittedUntrackedFileUri)
      .contains(projectDirPath.resolve(folderFile).toUri());
  }

  @Test
  void it_should_get_uncommited_file_in_sub_base_dir() throws GitAPIException, IOException {
    var folderFile = Path.of("folder").resolve("folderFile");
    var string = FilenameUtils.separatorsToUnix(folderFile.toString());
    createFile(projectDirPath, string, "line1", "line2", "line3");
    git.add().setUpdate(true).addFilepattern(string).call();

    var changedFiles = getVCSChangedFiles(projectDirPath.resolve("folder"));

    assertThat(changedFiles).hasSize(1)
      .contains(projectDirPath.resolve(folderFile).toUri());
  }

  @Test
  void it_should_return_empty_list_if_base_dir_not_resolved() {
    assertThat(getVCSChangedFiles(null)).isEmpty();
  }

  @Test
  void it_should_return_empty_list_on_git_exception(@TempDir Path nonGitDir) {
    assertThat(getVCSChangedFiles(nonGitDir)).isEmpty();
  }

  @Test
  void should_filter_ignored_files() throws IOException, GitAPIException {
    createFile(projectDirPath, "fileA", "line1", "line2", "line3");
    createFile(projectDirPath, "fileB", "line1", "line2", "line3");
    createFile(projectDirPath, "fileC", "line1", "line2", "line3");

    var fileAPath = Path.of("fileA");
    var fileBPath = Path.of("fileB");
    var fileCPath = Path.of("fileC");

    var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(projectDirPath);
    assertThat(Stream.of(fileAPath, fileBPath, fileCPath).filter(not(sonarLintGitIgnore::isFileIgnored)).toList())
      .hasSize(3)
      .containsExactly(fileAPath, fileBPath, fileCPath);

    addFileToGitIgnoreAndCommit(git, "fileB");

    sonarLintGitIgnore = GitService.createSonarLintGitIgnore(projectDirPath);
    assertThat(Stream.of(fileAPath, fileBPath, fileCPath).filter(not(sonarLintGitIgnore::isFileIgnored)).toList())
      .hasSize(2)
      .containsExactly(fileAPath, fileCPath);
  }

  @Test
  void should_filter_ignored_directories() throws IOException, GitAPIException {
    var fileA = Path.of("fileA");
    var fileB = Path.of("myDir").resolve("fileB");
    var fileC = Path.of("myDir").resolve("fileC");

    createFile(projectDirPath, "fileA", "line1", "line2", "line3");
    createFile(projectDirPath, fileB.toString(), "line1", "line2", "line3");
    createFile(projectDirPath, fileC.toString(), "line1", "line2", "line3");

    var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(projectDirPath);
    assertThat(Stream.of(fileA, fileB, fileC).filter(not(sonarLintGitIgnore::isFileIgnored)).toList())
      .hasSize(3)
      .containsExactly(fileA, fileB, fileC);

    addFileToGitIgnoreAndCommit(git, "myDir/");

    sonarLintGitIgnore = GitService.createSonarLintGitIgnore(projectDirPath);
    assertThat(Stream.of(fileA, fileB, fileC).filter(not(sonarLintGitIgnore::isFileIgnored)).toList())
      .hasSize(1)
      .containsExactly(fileA);
  }

  @Test
  void should_consider_all_files_not_ignored_on_gitignore() throws IOException {
    createFile(projectDirPath, "fileA", "line1", "line2", "line3");
    createFile(projectDirPath, "fileB", "line1", "line2", "line3");
    createFile(projectDirPath, "fileC", "line1", "line2", "line3");

    var fileAPath = projectDirPath.resolve("fileA");
    var fileBPath = projectDirPath.resolve("fileB");
    var fileCPath = projectDirPath.resolve("fileC");

    var gitIgnore = projectDirPath.resolve(GITIGNORE_FILENAME);
    FileUtils.deleteQuietly(gitIgnore.toFile());

    var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(projectDirPath);

    assertThat(logTester.logs(LogOutput.Level.INFO))
      .anyMatch(s -> s.contains(".gitignore file was not found for "));

    assertThat(Stream.of(fileAPath, fileBPath, fileCPath).filter(not(sonarLintGitIgnore::isFileIgnored)).toList())
      .hasSize(3)
      .containsExactly(fileAPath, fileBPath, fileCPath);
  }

  @Test
  void should_continue_normally_with_null_basedir() {
    var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(null);

    assertThat(sonarLintGitIgnore.isIgnored(Path.of("file/path"))).isFalse();
  }

  @Test
  void should_consider_files_ignored_when_git_root_above_project_root() throws IOException, GitAPIException {
    var gitRoot = Files.createTempDirectory("test");
    var projectRoot = Files.createDirectory(gitRoot.resolve("toto"));
    try (var ignored = Git.init().setDirectory(gitRoot.toFile()).call()) {
      var gitignoreFile = new File(gitRoot.toFile(), ".gitignore");
      Files.writeString(gitignoreFile.toPath(), "*.js");
    }

    var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(projectRoot);

    assertThat(sonarLintGitIgnore.isIgnored(Path.of("frontend/app/should_not_be_ignored.js"))).isTrue();
  }

  @Test
  void should_respect_gitignore_rules() throws IOException {
    Files.write(projectDirPath.resolve(GITIGNORE_FILENAME), List.of("app/", "!frontend/app/"), java.nio.file.StandardOpenOption.CREATE);
    var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(projectDirPath);

    assertThat(sonarLintGitIgnore.isIgnored(Path.of("frontend/app/should_not_be_ignored.js"))).isFalse();
    assertThat(sonarLintGitIgnore.isIgnored(Path.of("should_be_ignored.js"))).isFalse();
    assertThat(sonarLintGitIgnore.isIgnored(Path.of("app/should_be_ignored.js"))).isTrue();
  }

  @Test
  void createSonarLintGitIgnore_works_for_bare_repos_too() {
    var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(bareRepoPath);

    assertThat(sonarLintGitIgnore.isFileIgnored(Path.of("file.txt"))).isFalse();
    assertThat(sonarLintGitIgnore.isFileIgnored(Path.of("file.tmp"))).isTrue();
    assertThat(sonarLintGitIgnore.isFileIgnored(Path.of("file.log"))).isTrue();
  }

  @Test
  void nonAsciiCharacterFileName() {
    var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(bareRepoPath);

    assertThat(sonarLintGitIgnore.isIgnored(Path.of("Sönar.txt"))).isFalse();
    assertThat(sonarLintGitIgnore.isIgnored(Path.of("Sönar.log"))).isTrue();
  }

  @Test
  void should_not_read_git_ignore_on_bare_repo_with_no_commit(@TempDir Path bareRepoNoCommitPath) throws GitAPIException {
    try (var ignored = Git.init().setBare(true).setDirectory(bareRepoNoCommitPath.toFile()).call()) {
      var sonarLintGitIgnore = GitService.createSonarLintGitIgnore(bareRepoNoCommitPath);

      assertThat(sonarLintGitIgnore.isIgnored(Path.of("Sonar.txt"))).isFalse();
      assertThat(sonarLintGitIgnore.isIgnored(Path.of("Sonar.log"))).isFalse();
    }
  }

  @Test
  void git_blame_works_for_bare_repos_too() {
    var sonarLintBlameResult = blameWithGitFilesBlameLibrary(bareRepoPath, Stream.of("fileA", "fileB").map(Path::of).collect(Collectors.toSet()), null);

    assertThat(sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1, 2))).isPresent();
    assertThat(sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(3))).isEmpty();
    assertThat(sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of("fileB"), List.of(1, 2))).isPresent();
    assertThat(sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of("fileB"), List.of(3))).isEmpty();
  }

  @Test
  void should_return_empty_blame_result_if_no_commits_in_repo() throws IOException, GitAPIException {
    FileUtils.deleteDirectory(projectDirPath.resolve(".git").toFile());
    try (var ignored = Git.init().setDirectory(projectDirPath.toFile()).call()) {
      createFile(projectDirPath, "fileA", "line1", "line2", "line3");
      var filePath = Path.of("fileA");

      var sonarLintBlameResult = blameWithGitFilesBlameLibrary(projectDirPath, Set.of(filePath), null);

      assertThat(sonarLintBlameResult.getLatestChangeDateForLinesInFile(Path.of("fileA"), List.of(1))).isEmpty();
    }
  }

  @Test
  void it_should_only_return_files_under_baseDir() throws IOException, GitAPIException {
    //ACR-a6f28ee5d271400aae41370cd3ba4b02
    var rootFile = "rootFile.txt";
    var subDir = projectDirPath.resolve("subdir");
    Files.createDirectories(subDir);
    var subFile = subDir.resolve("subFile.txt");
    createFile(projectDirPath, rootFile, "root");
    createFile(subDir, "subFile.txt", "sub");

    //ACR-bee9e3a0643d4c25b57f0d80033bfd06
    git.add().addFilepattern(rootFile).call();
    git.add().addFilepattern("subdir/subFile.txt").call();
    commit(git, rootFile);
    commit(git, "subdir/subFile.txt");

    //ACR-0b110b44974c4b5eaccefa527d3290a2
    modifyFile(projectDirPath.resolve(rootFile), "root", "changed");
    modifyFile(subFile, "sub", "changed");

    //ACR-5d8adbb7b6c94e70b633c3a7db5b19f9
    var changedFiles = getVCSChangedFiles(subDir);

    assertThat(changedFiles)
      .contains(subFile.toUri())
      .doesNotContain(projectDirPath.resolve(rootFile).toUri());
  }

  @Test
  void it_should_get_remote_url() throws GitAPIException, URISyntaxException {
    //ACR-9c60c18de12343878527024afa6e090a
    var remoteUrl = "https://github.com/org/project.git";
    git.remoteAdd()
      .setName("origin")
      .setUri(new URIish(remoteUrl))
      .call();

    var retrievedUrl = GitService.getRemoteUrl(projectDirPath);

    assertThat(retrievedUrl).isEqualTo(remoteUrl);
  }

  @Test
  void it_should_return_null_when_no_origin_remote() {
    var retrievedUrl = GitService.getRemoteUrl(projectDirPath);

    assertThat(retrievedUrl).isNull();
  }

  @Test
  void it_should_return_null_for_null_base_dir() {
    var retrievedUrl = GitService.getRemoteUrl(null);

    assertThat(retrievedUrl).isNull();
  }

  @Test
  void it_should_return_null_for_non_git_directory(@TempDir Path nonGitDir) {
    var retrievedUrl = GitService.getRemoteUrl(nonGitDir);

    assertThat(retrievedUrl).isNull();
    assertThat(logTester.logs(LogOutput.Level.DEBUG))
      .anyMatch(s -> s.contains("Git repository not found for"));
  }

  @Test
  void it_should_get_remote_url_from_subdirectory() throws GitAPIException, IOException, URISyntaxException {
    var remoteUrl = "git@github.com:org/project.git";
    git.remoteAdd()
      .setName("origin")
      .setUri(new URIish(remoteUrl))
      .call();

    var subDir = projectDirPath.resolve("subdirectory");
    Files.createDirectories(subDir);

    var retrievedUrl = GitService.getRemoteUrl(subDir);

    assertThat(retrievedUrl).isEqualTo(remoteUrl);
  }

  @Test
  void it_should_return_null_when_config_access_fails() throws GitAPIException, URISyntaxException, IOException {
    var remoteUrl = "https://github.com/org/project.git";
    git.remoteAdd()
      .setName("origin")
      .setUri(new URIish(remoteUrl))
      .call();

    var gitConfigFile = projectDirPath.resolve(".git").resolve("config");
    Files.write(gitConfigFile, "invalid config content".getBytes());

    var retrievedUrl = GitService.getRemoteUrl(projectDirPath);

    assertThat(retrievedUrl).isNull();
    assertThat(logTester.logs(LogOutput.Level.DEBUG))
      .anyMatch(s -> s.contains("Error retrieving remote URL for"));
  }

}
