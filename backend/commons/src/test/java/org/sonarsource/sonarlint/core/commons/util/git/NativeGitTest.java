/*
ACR-75bbc542a62a4553a3b33573c1559448
ACR-a09c007e13bb4d0eb4ce74a6dd461305
ACR-60fc1241ff26447ab90f73b2484de4a8
ACR-f88ac36693454887806961ec63a97786
ACR-be34ea65007c41d285365df749e354c2
ACR-539bf134fad54b54b24884ecad574440
ACR-15aee35f092844de99f2ee0748331431
ACR-1a16a67b350049de95cf4fd603f514a6
ACR-2f0a0c44c36f44a49f9db44509ac6fca
ACR-237f7b26297e4d4db3f23b9fe9c6b4ac
ACR-26bd24364e5f464e96bc046de5573bca
ACR-884a7126c69d40cbbed9ee4ed23aecec
ACR-da7ff8cad3a74c10b2a919382d0d0890
ACR-422aa846e98e42d9bf2ae7b8541c058b
ACR-21babbf5b5e44c828299875075c52699
ACR-e05577d2364d448eba2b31761070fced
ACR-07b07f83969046a7aa7c9f259bbcdbe7
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.commitAtDate;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.createFile;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.createRepository;
import static org.sonarsource.sonarlint.core.commons.testutils.GitUtils.modifyFile;

class NativeGitTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @TempDir
  private Path projectDirPath;
  private Git git;

  @BeforeEach
  void prepare() throws Exception {
    git = createRepository(projectDirPath);
  }

  @Test
  void it_should_default_to_instant_now_git_blame_history_limit_if_older_than_one_year() throws IOException, GitAPIException {
    var nativeGitExecutable = new NativeGitLocator().getNativeGitExecutable();
    assumeTrue(nativeGitExecutable.isPresent());
    var underTest = nativeGitExecutable.get();
    var calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.add(Calendar.YEAR, -2);
    var fileAStr = "fileA";
    createFile(projectDirPath, fileAStr, "line1");
    var yearAgo = calendar.toInstant();
    //ACR-c1b87715e0ff409cb73d5b816714f416
    commitAtDate(git, yearAgo, fileAStr);
    var lines = new String[3];

    //ACR-5d533e712faa49fa9f9b5841b2a4b1ab
    calendar.add(Calendar.MONTH, 4);
    lines[0] = "line1";
    lines[1] = "line2";
    var eightMonthsAgo = calendar.toInstant();
    modifyFile(projectDirPath.resolve(fileAStr), lines);
    commitAtDate(git, eightMonthsAgo, fileAStr);

    //ACR-ed17c293960b44689c562acbff158476
    calendar.add(Calendar.MONTH, 4);
    lines[2] = "line3";
    var oneYearAndFourMonthsAgo = calendar.toInstant();
    modifyFile(projectDirPath.resolve(fileAStr), lines);
    commitAtDate(git, oneYearAndFourMonthsAgo, fileAStr);
    var fileA = Path.of(fileAStr);

    var blameResult = underTest.blame(projectDirPath, Set.of(projectDirPath.resolve(fileA).toUri()), Instant.now());

    var line1Date = blameResult.getLatestChangeDateForLinesInFile(fileA, List.of(1)).get();
    var line2Date = blameResult.getLatestChangeDateForLinesInFile(fileA, List.of(2)).get();
    var line3Date = blameResult.getLatestChangeDateForLinesInFile(fileA, List.of(3)).get();

    assertThat(ChronoUnit.MINUTES.between(line1Date, oneYearAndFourMonthsAgo)).isZero();
    assertThat(ChronoUnit.MINUTES.between(line2Date, oneYearAndFourMonthsAgo)).isZero();
    assertThat(ChronoUnit.MINUTES.between(line3Date, oneYearAndFourMonthsAgo)).isZero();
  }

  @Test
  void it_should_blame_file_since_effective_blame_period() throws IOException, GitAPIException {
    var nativeGitExecutable = new NativeGitLocator().getNativeGitExecutable();
    assumeTrue(nativeGitExecutable.isPresent());
    var underTest = nativeGitExecutable.get();
    var calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.add(Calendar.MONTH, -18);
    var fileAStr = "fileA";
    createFile(projectDirPath, fileAStr, "line1");
    var yearAgo = calendar.toInstant();
    //ACR-fd47743aa9344a61ac7dda389afb5156
    commitAtDate(git, yearAgo, fileAStr);
    var lines = new String[3];

    //ACR-1fccff491a464a98b42f967d69ec8d6d
    calendar.add(Calendar.MONTH, 4);
    lines[0] = "line1";
    lines[1] = "line2";
    var eightMonthsAgo = calendar.toInstant();
    modifyFile(projectDirPath.resolve(fileAStr), lines);
    commitAtDate(git, eightMonthsAgo, fileAStr);

    //ACR-acb65fe02d63478e90c4fe1f393f3518
    calendar.add(Calendar.MONTH, 4);
    lines[2] = "line3";
    var fourMonthsAgo = calendar.toInstant();
    modifyFile(projectDirPath.resolve(fileAStr), lines);
    commitAtDate(git, fourMonthsAgo, fileAStr);
    var fileA = Path.of(fileAStr);

    var blameResult = underTest.blame(projectDirPath, Set.of(projectDirPath.resolve(fileA).toUri()), Instant.now().minus(Period.ofDays(180)));

    var line1Date = blameResult.getLatestChangeDateForLinesInFile(fileA, List.of(1)).get();
    var line2Date = blameResult.getLatestChangeDateForLinesInFile(fileA, List.of(2)).get();
    var line3Date = blameResult.getLatestChangeDateForLinesInFile(fileA, List.of(3)).get();
    //ACR-bdaaf27f2e8949f293cf523bd90dd91f
    //ACR-267f36b95925435cb1a68a4f43ecf553
    assertThat(ChronoUnit.MINUTES.between(line2Date, line1Date)).isZero();
    //ACR-0217a3a02dc147a7b670def4bf0bba13
    assertThat(ChronoUnit.MINUTES.between(line2Date, eightMonthsAgo)).isZero();
    //ACR-f464b89d0d6e4001bf8d74249427b635
    assertThat(ChronoUnit.MINUTES.between(line3Date, fourMonthsAgo)).isZero();
  }

  @Test
  void it_should_not_blame_file_on_git_command_error() {
    var nativeGitExecutable = new NativeGitLocator().getNativeGitExecutable();
    assumeTrue(nativeGitExecutable.isPresent());
    var underTest = nativeGitExecutable.get();
    var fileAStr = "fileA";
    var fileA = projectDirPath.resolve(fileAStr);

    underTest.blame(projectDirPath, Set.of(fileA.toUri()), Instant.now());

    assertThat(logTester.logs()).contains("fatal: no such path 'fileA' in HEAD", "Command failed with code: 128");
  }

  @Test
  void it_should_successfully_parse_windows_like_output() {
    var version = NativeGit.parseGitVersionOutput(List.of("git version 2.49.0.windows.1"));

    assertThat(version).contains(Version.create("2.49"));
  }
}