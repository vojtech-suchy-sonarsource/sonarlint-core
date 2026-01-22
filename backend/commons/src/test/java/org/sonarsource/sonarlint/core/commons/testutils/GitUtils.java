/*
ACR-495afd2474734ab49a68d759b08a469c
ACR-7935114a94e74187a0b314cbb0b8250d
ACR-96381553f8fc4b90bf6472ffa92f5b51
ACR-430b36137a0b4a7687f9cea46aa5a68d
ACR-5466fe67ab664a08b1f93b7159c92642
ACR-79c2f1ca13174aacb319fd545d577c28
ACR-c5965451cff642289f96dd531b1b79ab
ACR-75f6d6ac7dde4be2be756aa589a9e447
ACR-a7ecc9fd85684d839d65bb61ad7e8ab7
ACR-1376092a994d42fb91e978adf843b2d6
ACR-6be2c2e873e14d14a07a662d10f3b73f
ACR-8ac4ad2f14174143b6b837eab25e2bb7
ACR-71cc851f5f404e11a47fe4456dce0f3a
ACR-2566afe051d24c98b7685c1b183cd7fd
ACR-bf808a39d93c4a9485705fdbf2e564ad
ACR-e0bfe09014484c51a7ceac2aea5c28df
ACR-0c3cf095c4534caa975ce98fc954a531
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
    //ACR-aa9503fb18fa411b81c65cffcb1cd0a3
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
    //ACR-469454cbfdd740eca740862fe3dbefcf
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
