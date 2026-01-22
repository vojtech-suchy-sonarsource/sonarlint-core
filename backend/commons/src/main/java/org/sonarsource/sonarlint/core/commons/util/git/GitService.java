/*
ACR-175e16d7fd39436ca1f69215d7524538
ACR-dc67bc2d2a834c439c89eef16c1d7201
ACR-ed6e16c8a15142f39bf460cf7a1e4549
ACR-ab6dc9e204894d3f9fbc8d47df0ccf90
ACR-8d083155bd3149ce95ce53762b387c80
ACR-501fe2a3f34141298ec750201e990403
ACR-b1b789f127c44997b4be5f8211f3e3b4
ACR-d6a7a8396d8349289bbde664a4d7ddda
ACR-eb3497ba03e34fafba6695ea444418c1
ACR-a2a8a1c04e4c4ae8ac0def23758899bd
ACR-cff5ef36b008438b8c91c5dc305f5d9e
ACR-095c7230d8214b08b0f3ac1f9e8dbc13
ACR-31be2a4cfb0c4a439cdafdbbccbab391
ACR-a0561bb82a2a4d27964c7374444796fe
ACR-9eb737c907484ccc827a8078b795900b
ACR-c0467718bd5a4cc98de9d17c8a7847b7
ACR-8c0f409709204152bfabc45e495d65f1
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

  //ACR-fb01b4267f214af8a340f3c60b282ae5
  //ACR-6c01d44bbab54f42bf227819c98ae284
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

  /*ACR-507122d99d0f4cb7903252628335dfcc
ACR-bd7d8ef385534f258499d972023ab298
ACR-67b3973fcb314678be93586e2e42129b
ACR-6a507a2f04ea4b048dd3b074dbb8ed30
ACR-8ba0bccb82094ee8af264b3b3ae54a2a
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
      //ACR-4ac4ef61f0ee48d197d60dc44a58d7bc
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
        //ACR-5864ab1468784f59a5348ddc6b521b67
        objReader.getShallowCommits();
        return repository;
      }
    } catch (IOException e) {
      throw new IllegalStateException("Unable to open Git repository", e);
    }
  }

  /*ACR-83bed9b6d7204bcab06c685201390cf2
ACR-f87bb1db6b73417a926d2840a4764371
ACR-220fa85f2b9f40319197b660e6e00059
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
      //ACR-2cf627a346774cbebac3f12ce233c0b9
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
