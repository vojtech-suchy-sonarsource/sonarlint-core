/*
ACR-c0e9d4b630f9488799aa2f63d3fd6240
ACR-dbbb383c393f44bd86939db898df8ed4
ACR-22bf1bb13c8943cc9c1acb16c217a738
ACR-790f041bb95a4a6db913d677f002952a
ACR-ce3dfb3df6514103b8ffd2d514f6c450
ACR-e9cdf55990f847fa9489115e5afa4a9d
ACR-afdf8ad3da504dcea1acf73b994b9ff5
ACR-2c40cb5e99ea42ec8c589983347ca495
ACR-7e894b0065ee4184b5530e7052384ad1
ACR-3f542f0fb3ce442dbf8028518df338e0
ACR-76f1cf230f68435ca46e4ec33e23ffaf
ACR-44c90d5be82c48f2871491410147a004
ACR-8294e27417fa4b52932ff9fb6b0c62fe
ACR-02d308f2f7a648dbbbb83ce6948e68df
ACR-083b61587b8b4b46804d479f5ec28859
ACR-d0dde7cba5624bac9d07bc03a2d94848
ACR-f7141f806ac146d0918c6d3afdf5f1b0
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.ignore.IgnoreNode;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.sonar.scm.git.blame.RepositoryBlameCommand;
import org.sonarsource.sonarlint.core.commons.MultiFileBlameResult;
import org.sonarsource.sonarlint.core.commons.SonarLintGitIgnore;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.util.git.exceptions.GitException;
import org.sonarsource.sonarlint.core.commons.util.git.exceptions.GitRepoNotFoundException;

import static java.util.Optional.ofNullable;
import static org.eclipse.jgit.lib.Constants.GITIGNORE_FILENAME;

public class GitService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final int FILES_GIT_BLAME_TRIGGER_THRESHOLD = 10;

  private final NativeGitLocator nativeGitLocator;

  GitService(NativeGitLocator nativeGitLocator) {
    this.nativeGitLocator = nativeGitLocator;
  }

  public static GitService create() {
    return new GitService(new NativeGitLocator());
  }

  public MultiFileBlameResult getBlameResult(Path projectBaseDir, Set<Path> projectBaseRelativeFilePaths, Set<URI> fileUris, @Nullable UnaryOperator<String> fileContentProvider,
    Instant thresholdDate) {

    var nativeGitExecutable = nativeGitLocator.getNativeGitExecutable();
    if (nativeGitExecutable.isEmpty() || fileUris.size() >= FILES_GIT_BLAME_TRIGGER_THRESHOLD) {
      return blameWithGitFilesBlameLibrary(projectBaseDir, projectBaseRelativeFilePaths, fileContentProvider);
    }
    return nativeGitExecutable.get().blame(projectBaseDir, fileUris, thresholdDate);
  }

  //ACR-99046ee5d70c4a7abb45e3786400abff
  //ACR-0f9addc5c73247e6bf5d2672147eac62
  public static Set<URI> getVCSChangedFiles(@Nullable Path baseDir) {
    if (baseDir == null) {
      return Set.of();
    }
    try {
      var repo = buildGitRepository(baseDir);
      var workTreePath = repo.getWorkTree().toPath();
      var git = new Git(repo);
      var status = git.status().call();
      var uncommitted = status.getUncommittedChanges().stream();
      var untracked = status.getUntracked().stream().filter(f -> !f.equals(GITIGNORE_FILENAME));
      return Stream.concat(uncommitted, untracked)
        .map(workTreePath::resolve)
        .filter(path -> path.normalize().startsWith(baseDir.normalize()))
        .map(Path::toUri)
        .collect(Collectors.toSet());
    } catch (GitAPIException | GitException e) {
      LOG.debug("Git repository access error: ", e);
      return Set.of();
    }
  }

  /*ACR-25ca618ade9840b79e23ea0f419aced9
ACR-18d0fbd4a2e249e2b3f00333a1d7ce40
ACR-6a8e7d5605e142979fb73b00c5cb8a72
ACR-dd1d79fb91f14e50b889f99975c698ff
ACR-ad9ab24792654aacbb49f5a663b6e65e
   */
  @CheckForNull
  public static String getRemoteUrl(@Nullable Path baseDir) {
    if (baseDir == null) {
      return null;
    }

    try (var gitRepo = buildGitRepository(baseDir)) {
      var config = gitRepo.getConfig();
      return config.getString("remote", "origin", "url");
    } catch (GitRepoNotFoundException e) {
      LOG.debug("Git repository not found for {}", baseDir);
      return null;
    } catch (Exception e) {
      LOG.debug("Error retrieving remote URL for {}: {}", baseDir, e.getMessage());
      return null;
    }
  }

  public static MultiFileBlameResult blameWithGitFilesBlameLibrary(Path projectBaseDir, Set<Path> projectBaseRelativeFilePaths,
    @Nullable UnaryOperator<String> fileContentProvider) {
    LOG.debug("Falling back to JGit");
    var startTime = System.currentTimeMillis();
    var gitRepo = buildGitRepository(projectBaseDir);

    var gitRepoRelativeProjectBaseDir = getRelativePath(gitRepo, projectBaseDir);

    var gitRepoRelativeFilePaths = projectBaseRelativeFilePaths.stream()
      .map(gitRepoRelativeProjectBaseDir::resolve)
      .map(Path::toString)
      .map(FilenameUtils::separatorsToUnix)
      .collect(Collectors.toSet());

    var blameCommand = new RepositoryBlameCommand(gitRepo)
      .setTextComparator(RawTextComparator.WS_IGNORE_ALL)
      .setMultithreading(true)
      .setFilePaths(gitRepoRelativeFilePaths);
    ofNullable(fileContentProvider)
      .ifPresent(provider -> blameCommand.setFileContentProvider(adaptToPlatformBasedPath(provider)));

    try {
      var blameResult = blameCommand.call();
      var multiFileBlameResult = new MultiFileBlameResult(
        blameResult.getFileBlameByPath().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new BlameResult(Arrays.asList(e.getValue().getCommitDates())))),
        gitRepoRelativeProjectBaseDir);
      LOG.debug("Blamed {} files in {}ms", projectBaseRelativeFilePaths.size(), System.currentTimeMillis() - startTime);
      return multiFileBlameResult;
    } catch (NoHeadException e) {
      //ACR-32e647af05a44d09a97731465a50d120
      return MultiFileBlameResult.empty(gitRepoRelativeProjectBaseDir);
    } catch (GitAPIException e) {
      throw new IllegalStateException("Failed to blame repository files", e);
    }
  }

  private static Path getRelativePath(Repository gitRepo, Path projectBaseDir) {
    var repoDir = gitRepo.isBare() ? gitRepo.getDirectory() : gitRepo.getWorkTree();
    return repoDir.toPath().relativize(projectBaseDir);
  }

  private static Repository buildGitRepository(Path basedir) {
    try {
      var repositoryBuilder = new RepositoryBuilder()
        .findGitDir(basedir.toFile());
      if (ofNullable(repositoryBuilder.getGitDir()).isEmpty()) {
        throw new GitRepoNotFoundException(basedir.toString());
      }

      var repository = repositoryBuilder.build();
      try (var objReader = repository.getObjectDatabase().newReader()) {
        //ACR-f821db3163c541e78982a844ac0cf2f6
        objReader.getShallowCommits();
        return repository;
      }
    } catch (IOException e) {
      throw new IllegalStateException("Unable to open Git repository", e);
    }
  }

  /*ACR-b846d34faa99436cb611e6f56e9b5b25
ACR-900397464f754e968694c68be36b2ab6
ACR-0a5314434f974529a69a8e1c6351973c
   */
  public static SonarLintGitIgnore createSonarLintGitIgnore(@Nullable Path baseDir) {
    if (baseDir == null) {
      return new SonarLintGitIgnore(new IgnoreNode());
    }
    try {
      var gitRepo = buildGitRepository(baseDir);
      var ignoreNode = buildIgnoreNode(gitRepo);
      return new SonarLintGitIgnore(ignoreNode);
    } catch (GitRepoNotFoundException e) {
      LOG.info("Git Repository not found for {}. The path {} is not in a Git repository", baseDir, e.getPath());
    } catch (FileNotFoundException e) {
      LOG.info(".gitignore file was not found for {}", baseDir);
    } catch (Exception e) {
      LOG.warn("Error occurred while reading .gitignore file: ", e);
      LOG.warn("Building empty ignore node with no rules. Files checked against this node will be considered as not ignored.");
    }
    return new SonarLintGitIgnore(new IgnoreNode());
  }

  private static IgnoreNode buildIgnoreNode(Repository repository) throws IOException {
    var ignoreNode = new IgnoreNode();
    if (repository.isBare()) {
      readGitIgnoreFileFromBareRepo(repository, ignoreNode);
    } else {
      readIgnoreFileFromNonBareRepo(repository, ignoreNode);
    }
    return ignoreNode;
  }

  private static void readGitIgnoreFileFromBareRepo(Repository repository, IgnoreNode ignoreNode) throws IOException {
    var loader = readFileContentFromGitRepo(repository, GITIGNORE_FILENAME);
    if (loader.isPresent()) {
      try (InputStream inputStream = loader.get().openStream()) {
        ignoreNode.parse(inputStream);
      }
    }
  }

  private static void readIgnoreFileFromNonBareRepo(Repository repository, IgnoreNode ignoreNode) throws IOException {
    var rootDir = repository.getWorkTree();
    var gitIgnoreFile = new File(rootDir, GITIGNORE_FILENAME);
    try (var inputStream = new FileInputStream(gitIgnoreFile)) {
      ignoreNode.parse(inputStream);
    }
  }

  private static UnaryOperator<String> adaptToPlatformBasedPath(UnaryOperator<String> provider) {
    return unixPath -> {
      var platformBasedPath = Path.of(unixPath).toString();
      return provider.apply(platformBasedPath);
    };
  }

  private static Optional<ObjectLoader> readFileContentFromGitRepo(Repository repository, String fileName) throws IOException {
    var headId = repository.resolve(Constants.HEAD);
    if (headId == null) {
      //ACR-1f78310a28d541708165453565ae8cbe
      return Optional.empty();
    }

    try (var revWalk = new RevWalk(repository)) {
      var commit = revWalk.parseCommit(headId);

      try (var treeWalk = new TreeWalk(repository)) {
        treeWalk.addTree(commit.getTree());
        treeWalk.setRecursive(true);
        treeWalk.setFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(fileName));

        if (!treeWalk.next()) {
          return Optional.empty();
        }

        return Optional.of(repository.open(treeWalk.getObjectId(0)));
      }
    }
  }

}
