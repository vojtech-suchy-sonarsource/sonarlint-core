/*
ACR-9d118afd41c649a69d73700c2a6c34c5
ACR-68bbd6c9d4814e95aad24dbcf5eb0f26
ACR-bc8ac48352b84b7c980a729b851b6457
ACR-a727f2dfb54645be994d910c64c918d7
ACR-c4e661c4535c4ebca527850ed225d923
ACR-809c646e1c1b4713b0d87f528ec35c2f
ACR-7c8b7c5531d6411389da50a428246afe
ACR-77c8fb6e82804e4986a894566dbb9c1e
ACR-03b9b45e923045d3aeff21ba09855ff0
ACR-f740b827c8a84ba4a4b483c9f329c24c
ACR-d3a510fa90b8468e81c5c7f6a0bbfa06
ACR-31aad0d6a4ba40649c88bda13b9198c8
ACR-14938911291a4e95a1f972fc56ae34cf
ACR-6424066a6baf4b4cac97257daec44cc9
ACR-83129e76a1044538b470000d3b260e13
ACR-03c235e36f644509913a9b83c2ae4ec5
ACR-8ee35c3ced064669bbf529bd2c2a13e1
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
    //ACR-e1ed528c8d954e2598e76124eb025e86
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
        //ACR-3361be9ac5f94506b5dbff3b9f9cc50a
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
        //ACR-c0cf99e23a004a918690b215fa77f0e6
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
