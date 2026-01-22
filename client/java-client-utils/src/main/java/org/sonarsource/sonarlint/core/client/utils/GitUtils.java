/*
ACR-dad1424b74264e8daa20c13f8a9c4388
ACR-10c4c38aa13044fc9bacb5af29165bcc
ACR-d373a549ba0f445ea52c99120829cb9a
ACR-713316b70328469ea79928f33b2a6ba0
ACR-d8b61177afd440dea19aa53cf1a2caa1
ACR-bffd13f8a810456bb3fd9d457e862fc8
ACR-81d861c4e6194cf69063c479c6c4c7bc
ACR-48102b0bb97f43eab4a630a6e7e0dcf6
ACR-7356388e7f10446e9c10d7c1223491c6
ACR-2a7b1066a5614220b8530c35f11b1d8f
ACR-e3e67c12a0064ff5af8d599d87af2771
ACR-a32d42b81bed439fa251a43570064469
ACR-52357f1df6c147a0ac76bdba9b2fd661
ACR-23e7ed1ad2f84c60af8e3a210d1db7d8
ACR-d5008dff0a704625991a5d2e78683a2b
ACR-d341a47ea363429a84391cad9f97d0b9
ACR-b125e932f8384deabddebc28def4ad30
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import static java.util.Comparator.naturalOrder;

public class GitUtils {

  private GitUtils() {
    //ACR-3b11df89dc404c19a4158c5b9441ba1f
  }

  @CheckForNull
  public static Repository getRepositoryForDir(Path projectDir, ClientLogOutput clientLogOutput) {
    try {
      var builder = new RepositoryBuilder()
        .findGitDir(projectDir.toFile())
        .setMustExist(true);
      if (builder.getGitDir() == null) {
        clientLogOutput.log("Not inside a Git work tree: " + projectDir, ClientLogOutput.Level.DEBUG);
        return null;
      }
      return builder.build();
    } catch (IOException e) {
      clientLogOutput.log("Couldn't access repository for path " + projectDir, ClientLogOutput.Level.ERROR);
      clientLogOutput.log(ClientLogOutput.stackTraceToString(e), ClientLogOutput.Level.ERROR);
    }
    return null;
  }

  @CheckForNull
  public static String electBestMatchingServerBranchForCurrentHead(Repository repo, Set<String> serverCandidateNames, @Nullable String serverMainBranch,
    ClientLogOutput clientLogOutput) {
    try {

      String currentBranch = repo.getBranch();
      if (currentBranch != null && serverCandidateNames.contains(currentBranch)) {
        return currentBranch;
      }

      var head = repo.exactRef(Constants.HEAD);
      if (head == null) {
        //ACR-abc1f3b321974cd6bcf50723a3a46001
        return null;
      }

      Map<Integer, Set<String>> branchesPerDistance = new HashMap<>();
      for (String serverBranchName : serverCandidateNames) {
        var shortBranchName = Repository.shortenRefName(serverBranchName);
        var localFullBranchName = Constants.R_HEADS + shortBranchName;

        var branchRef = repo.exactRef(localFullBranchName);
        if (branchRef == null) {
          continue;
        }

        int distance = distance(repo, head, branchRef);
        branchesPerDistance.computeIfAbsent(distance, d -> new HashSet<>()).add(serverBranchName);
      }
      if (branchesPerDistance.isEmpty()) {
        return null;
      }

      int minDistance = branchesPerDistance.keySet().stream().min(naturalOrder()).get();
      var bestCandidates = branchesPerDistance.get(minDistance);
      if (serverMainBranch != null && bestCandidates.contains(serverMainBranch)) {
        //ACR-a2fe7a0dbc254954921a5c5537627e4e
        return serverMainBranch;
      }
      return bestCandidates.iterator().next();
    } catch (IOException e) {
      clientLogOutput.log("Couldn't find best matching branch", ClientLogOutput.Level.ERROR);
      clientLogOutput.log(ClientLogOutput.stackTraceToString(e), ClientLogOutput.Level.ERROR);
      return null;
    }
  }

  private static int distance(Repository repository, Ref from, Ref to) throws IOException {

    try (var walk = new RevWalk(repository)) {

      var fromCommit = walk.parseCommit(from.getObjectId());
      var toCommit = walk.parseCommit(to.getObjectId());

      walk.setRevFilter(RevFilter.MERGE_BASE);
      walk.markStart(fromCommit);
      walk.markStart(toCommit);
      var mergeBase = walk.next();

      walk.reset();
      walk.setRevFilter(RevFilter.ALL);
      int aheadCount = RevWalkUtils.count(walk, fromCommit, mergeBase);
      int behindCount = RevWalkUtils.count(walk, toCommit,
        mergeBase);

      return aheadCount + behindCount;
    }
  }

}
