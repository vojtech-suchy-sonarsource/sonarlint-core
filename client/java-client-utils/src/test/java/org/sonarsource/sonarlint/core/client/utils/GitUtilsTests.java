/*
ACR-fd6e734505fe43dd8e88f813047eb278
ACR-b2fc2cea0aeb45ca89016be6604c7096
ACR-7a6ba1f57f454fa3aafa9e7c52e39e6c
ACR-87775a2334034463b892c1503c3e0476
ACR-5c62b37e433f41859a5576b3af8cd15c
ACR-81eeafe3641c4ba984cc81843e1e1420
ACR-b33d32b8984c49569189cc7fe23742fa
ACR-8043541f5b0046f886dce8e4246a4296
ACR-b5b8a4ef8a7e491f83ef799ee30aa948
ACR-2a6366d8eaee40a0bddb74d9e2d81ab8
ACR-b5c5f5664aa54614a9cc62ca3791838e
ACR-d4600abd226f49aa8e0054513d618851
ACR-dad1681b3f064009b30e6744f0faf220
ACR-f7003456663742d8bb3ee0555bf186d2
ACR-f24f11af8c5545378911283396194eff
ACR-f1d66a462f5547c0a5540d62744a0108
ACR-b28fd76bbd4b43c38c78514c983492d0
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
    //ACR-1c0e8f38d8064fbd8504880af2dc8550
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
