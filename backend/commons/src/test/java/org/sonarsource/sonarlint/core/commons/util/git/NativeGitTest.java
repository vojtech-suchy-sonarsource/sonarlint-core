/*
ACR-fa73177f13ea4ae7b9591018735a926c
ACR-3a469916b884440db0a9b571bd51931a
ACR-d552a93ab33b4af695ca0dfb417fc2af
ACR-d0bd32e2b2ad4489885daf59e2e0814e
ACR-d4d8dfabf75c457c9cdcba8edca75f5e
ACR-90b16d75ec8f4b3ca5dbac881c7072e9
ACR-db7e7cfd6f1149a88cc4e9f2c09867ad
ACR-223e7a1fe3e44038bfed764503975629
ACR-45bfddc864e94a1c99283db0160cbd14
ACR-0cb7eb9788b14c2c9203408911759764
ACR-1ce413b5c1554f95bab11cf0525f0bff
ACR-5657bfc2ae2541ff826e0ccf5ea15d28
ACR-c5f800e9e8e1449897e1b7691d6f08d9
ACR-9c60b6129393463ea409cd926989f454
ACR-28b8f9f8094e4fa293309a19c29e0b57
ACR-3a07e225ea374f60b11af65fbd986a1c
ACR-d17a8bf659d8483ab5114bba5d844bbc
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
    //ACR-6b6a80f493744a6e9d4a9f9aac151458
    commitAtDate(git, yearAgo, fileAStr);
    var lines = new String[3];

    //ACR-40b7300562424ba9b3586a04c0314bac
    calendar.add(Calendar.MONTH, 4);
    lines[0] = "line1";
    lines[1] = "line2";
    var eightMonthsAgo = calendar.toInstant();
    modifyFile(projectDirPath.resolve(fileAStr), lines);
    commitAtDate(git, eightMonthsAgo, fileAStr);

    //ACR-05fba323aa6c4eb1aeb698984a630441
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
    //ACR-9515976263014e5c84266b1e3ccd844c
    commitAtDate(git, yearAgo, fileAStr);
    var lines = new String[3];

    //ACR-7584b822492944289e7e7f77a88977ed
    calendar.add(Calendar.MONTH, 4);
    lines[0] = "line1";
    lines[1] = "line2";
    var eightMonthsAgo = calendar.toInstant();
    modifyFile(projectDirPath.resolve(fileAStr), lines);
    commitAtDate(git, eightMonthsAgo, fileAStr);

    //ACR-975c7f70fba64c89bf147c4d65c1c20c
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
    //ACR-d291ae121e1942f78ea50c6a5e27db82
    //ACR-d5d2a5cccaca437f9e14bcd17860cc24
    assertThat(ChronoUnit.MINUTES.between(line2Date, line1Date)).isZero();
    //ACR-162d5491f408405183820cdfe5d90a4f
    assertThat(ChronoUnit.MINUTES.between(line2Date, eightMonthsAgo)).isZero();
    //ACR-2eefaebca5e14e4f90786fbeecf08e3b
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