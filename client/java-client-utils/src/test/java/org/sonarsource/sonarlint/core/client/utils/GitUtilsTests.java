/*
ACR-bdfb4bd710ae47049b5b0953ede17013
ACR-b42ede4d6d3b4df98e525fa5ba850266
ACR-0ac973a7747746a4b4cfe8302406eec1
ACR-f76d7d24e5eb4a928a73b226cd530e95
ACR-326738bacf6b447c95083399c1c318f1
ACR-a05ce50e5e214c1794be2369f6e24523
ACR-66af8ba1561a42d59f415fe66c6758a3
ACR-3ffa53a4e763439baa46172ad2013ae9
ACR-1e6c5a8aad814ae2a3e820adf15c2147
ACR-d06a44ffe6a34d1c947cf967e97838ee
ACR-0e2bd5daa24b47c7a5ff5d63ae1cbb5f
ACR-8de8d53574e24a36a93262fd59fc985a
ACR-42944a58068b4cbc8c8ef2422f50176c
ACR-861da52bde5d4b3d88eeeae7f1cc3271
ACR-bb17535b2d39483dae8295846dd7485b
ACR-2ef999db1f3c4dab8c13c97008d9be6a
ACR-c3e95b05d8de47c6ad35fd6ba2afeb18
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GitUtilsTests {

  private final ClientLogOutput fakeClientLogger = (m, l) -> {
  };

  @Test
  void noGitRepoShouldBeNull(@TempDir File projectDir) throws IOException {
    javaUnzip("no-git-repo.zip", projectDir);
    Path path = Paths.get(projectDir.getPath(), "no-git-repo");
    Repository repo = GitUtils.getRepositoryForDir(path, fakeClientLogger);
    assertThat(repo).isNull();
  }

  @Test
  void gitRepoShouldBeNotNull(@TempDir File projectDir) throws IOException {
    javaUnzip("dummy-git.zip", projectDir);
    Path path = Paths.get(projectDir.getPath(), "dummy-git");
    try (Repository repo = GitUtils.getRepositoryForDir(path, fakeClientLogger)) {
      Set<String> serverCandidateNames = Set.of("foo", "bar", "master");

      String branch = GitUtils.electBestMatchingServerBranchForCurrentHead(repo, serverCandidateNames, "master", fakeClientLogger);
      assertThat(branch).isEqualTo("master");
    }
  }

  @Test
  void shouldElectAnalyzedBranch(@TempDir File projectDir) throws IOException {
    javaUnzip("analyzed-branch.zip", projectDir);
    Path path = Paths.get(projectDir.getPath(), "analyzed-branch");
    try (Repository repo = GitUtils.getRepositoryForDir(path, fakeClientLogger)) {
      Set<String> serverCandidateNames = Set.of("foo", "closest_branch", "master");

      String branch = GitUtils.electBestMatchingServerBranchForCurrentHead(repo, serverCandidateNames, "master", fakeClientLogger);
      assertThat(branch).isEqualTo("closest_branch");
    }
  }

  @Test
  void shouldReturnNullIfNonePresentInLocalGit(@TempDir File projectDir) throws IOException {
    javaUnzip("analyzed-branch.zip", projectDir);
    Path path = Paths.get(projectDir.getPath(), "analyzed-branch");
    try (Repository repo = GitUtils.getRepositoryForDir(path, fakeClientLogger)) {
      Set<String> serverCandidateNames = Set.of("unknown1", "unknown2", "unknown3");

      String branch = GitUtils.electBestMatchingServerBranchForCurrentHead(repo, serverCandidateNames, "master", fakeClientLogger);
      assertThat(branch).isNull();
    }
  }

  @Test
  void shouldElectClosestBranch(@TempDir File projectDir) throws IOException {
    javaUnzip("closest-branch.zip", projectDir);
    Path path = Paths.get(projectDir.getPath(), "closest-branch");

    try (Repository repo = GitUtils.getRepositoryForDir(path, fakeClientLogger)) {

      Set<String> serverCandidateNames = Set.of("foo", "closest_branch", "master");

      String branch = GitUtils.electBestMatchingServerBranchForCurrentHead(repo, serverCandidateNames, "master", fakeClientLogger);
      assertThat(branch).isEqualTo("closest_branch");
    }
  }

  @Test
  void shouldElectClosestBranch_even_if_no_main_branch(@TempDir File projectDir) throws IOException {
    javaUnzip("closest-branch.zip", projectDir);
    Path path = Paths.get(projectDir.getPath(), "closest-branch");

    try (Repository repo = GitUtils.getRepositoryForDir(path, fakeClientLogger)) {

      Set<String> serverCandidateNames = Set.of("foo", "closest_branch", "master");

      String branch = GitUtils.electBestMatchingServerBranchForCurrentHead(repo, serverCandidateNames, null, fakeClientLogger);
      assertThat(branch).isEqualTo("closest_branch");
    }
  }

  @Test
  void shouldElectMainBranchForNonAnalyzedChildBranch(@TempDir File projectDir) throws IOException {
    javaUnzip("child-from-non-analyzed.zip", projectDir);
    Path path = Paths.get(projectDir.getPath(), "child-from-non-analyzed");
    try (Repository repo = GitUtils.getRepositoryForDir(path, fakeClientLogger)) {

      Set<String> serverCandidateNames = Set.of("foo", "branch_to_analyze", "master");

      String branch = GitUtils.electBestMatchingServerBranchForCurrentHead(repo, serverCandidateNames, "master", fakeClientLogger);
      assertThat(branch).isEqualTo("master");
    }
  }

  @Test
  void shouldReturnNullOnException() throws IOException {
    Repository repo = mock(Repository.class);
    RefDatabase db = mock(RefDatabase.class);
    when(repo.getRefDatabase()).thenReturn(db);
    when(db.exactRef(anyString())).thenThrow(new IOException());

    String branch = GitUtils.electBestMatchingServerBranchForCurrentHead(repo, Set.of("foo", "bar", "master"), "master", fakeClientLogger);

    assertThat(branch).isNull();
  }

  @Test
  void shouldFavorCurrentBranchIfMultipleCandidates(@TempDir File projectDir) throws IOException {
    //ACR-1155408facb64bc9adf34d4efb7fab65
    javaUnzip("two-branches-for-head.zip", projectDir);
    Path path = Paths.get(projectDir.getPath(), "two-branches-for-head");
    try (Repository repo = GitUtils.getRepositoryForDir(path, fakeClientLogger)) {

      Set<String> serverCandidateNames = Set.of("main", "same-as-master", "another");

      String branch = GitUtils.electBestMatchingServerBranchForCurrentHead(repo, serverCandidateNames, "main", fakeClientLogger);
      assertThat(branch).isEqualTo("same-as-master");
    }
  }

  public void javaUnzip(String zipFileName, File toDir) throws IOException {
    File testRepos = new File("src/test/test-repos");
    File zipFile = new File(testRepos, zipFileName);
    javaUnzip(zipFile, toDir);
  }

  private static void javaUnzip(File zip, File toDir) {
    try {
      try (ZipFile zipFile = new ZipFile(zip)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          File to = new File(toDir, entry.getName());
          if (entry.isDirectory()) {
            forceMkdir(to);
          } else {
            File parent = to.getParentFile();
            forceMkdir(parent);

            Files.copy(zipFile.getInputStream(entry), to.toPath());
          }
        }
      }
    } catch (Exception e) {
      throw new IllegalStateException(format("Fail to unzip %s to %s", zip, toDir), e);
    }
  }

  private static void forceMkdir(final File directory) throws IOException {
    if ((directory != null) && (!directory.mkdirs() && !directory.isDirectory())) {
      throw new IOException("Cannot create directory '" + directory + "'.");
    }
  }
}
