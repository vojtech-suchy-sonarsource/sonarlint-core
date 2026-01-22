/*
ACR-921939dc525e42aabe059a7a4aac6330
ACR-0e408983ab764e3e8c84beea2d4db796
ACR-7e8469a86fec4a74b1bec291f03d5228
ACR-ffd5afab18b1473a9990e2ba46892836
ACR-b4a001c2336d40b79ecddfe02dec584e
ACR-a5cd6c9871f14da0b6def4f271adeedd
ACR-4b9a13aaa6ce44c1a49662421fe8c235
ACR-3f27021e2ef6417ebd9e32f466eff22b
ACR-46d2c9a124914ab6b7aaa7db197d2ab7
ACR-cb5f641e1018477b94b2396f33c57eb1
ACR-f2070315918343c593caa95d1ba1e6c0
ACR-a105a1524a3f47d899afa91dd8607694
ACR-fb887b33284c414aba24a4417805535e
ACR-fe50a9b66a304973acfc102561150cf3
ACR-3ad83b91ad2a4436b735c15eef6ae47f
ACR-091016af26a74268a809dae4453a290a
ACR-c06d1c9be0fe4398b83aac7ea5f11abf
 */
package org.sonarsource.sonarlint.core.commons.testutils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.SystemReader;

public class GitUtils {

  private GitUtils() {
    //ACR-e90621a6fe8f4920b5336b7baa67f4f0
  }

  public static Git createRepository(Path worktree) throws GitAPIException, IOException {
    var repo = FileRepositoryBuilder.create(worktree.resolve(".git").toFile());
    repo.create();
    var git = new Git(repo);
    createEmptyGitIgnoreFile(git);
    return git;
  }

  private static void createEmptyGitIgnoreFile(Git git) throws GitAPIException, IOException {
    var gitIgnoreFile = getGitIgnoreFile(git);
    if (gitIgnoreFile.createNewFile()) {
      git.add().addFilepattern(Constants.GITIGNORE_FILENAME);
      git.commit().setMessage("Add empty .gitignore").call();
    }
  }

  public static void addFileToGitIgnoreAndCommit(Git git, String filePath) throws IOException, GitAPIException {
    var gitIgnoreFile = getGitIgnoreFile(git);
    //ACR-73533efd5d6c4299809af6a6ca9888d2
    try (var writer = new FileWriter(gitIgnoreFile, true)) {
      writer.write("\n" + filePath + "\n");
    }
    commit(git, gitIgnoreFile.getPath());
  }

  private static File getGitIgnoreFile(Git git) {
    return new File(git.getRepository().getDirectory().getParent(), Constants.GITIGNORE_FILENAME);
  }

  public static Instant commit(Git git, String... paths) throws GitAPIException {
    return commit(git, Instant.now(), paths);
  }

  public static Instant commit(Git git, Instant commitDate, String... paths) throws GitAPIException {
    return commitObject(git, commitDate, paths).getCommitterIdent().getWhenAsInstant();
  }

  private static RevCommit commitObject(Git git, Instant commitDate, String... paths) throws GitAPIException {
    if (paths.length > 0) {
      var add = git.add();
      for (String p : paths) {
        add.addFilepattern(FilenameUtils.separatorsToUnix(p));
      }
      add.call();
    }
    return git.commit().setCommitter(new PersonIdent("joe", "email@email.com", commitDate, ZoneId.systemDefault())).setMessage("msg").call();
  }

  public static Instant commitAtDate(Git git, Instant commitDate, String... paths) throws GitAPIException {
    if (paths.length > 0) {
      var add = git.add();
      for (String p : paths) {
        add.addFilepattern(FilenameUtils.separatorsToUnix(p));
      }
      add.call();
    }

    var commit = git.commit()
      .setCommitter(new PersonIdent("joe", "email@email.com", commitDate, SystemReader.getInstance().getTimeZoneAt(commitDate)))
      .setMessage("msg")
      .call();
    return commit.getCommitterIdent().getWhenAsInstant();
  }

  public static void createFile(Path worktree, String relativePath, String... lines) throws IOException {
    var newFile = worktree.resolve(relativePath);
    Files.createDirectories(newFile.getParent());
    var content = String.join(System.lineSeparator(), lines) + System.lineSeparator();
    Files.write(newFile, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  public static void appendFile(Path file, String... lines) throws IOException {
    var content = String.join(System.lineSeparator(), lines) + System.lineSeparator();
    Files.write(file, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
  }

  public static void modifyFile(Path file, String... lines) throws IOException {
    var content = String.join(System.lineSeparator(), lines) + System.lineSeparator();
    Files.write(file, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
  }

}
