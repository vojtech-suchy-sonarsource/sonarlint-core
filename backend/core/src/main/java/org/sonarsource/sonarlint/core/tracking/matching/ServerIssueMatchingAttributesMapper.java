/*
ACR-890d077368f94c9a838b851b384282ab
ACR-ece99479d73f4cddb020aec4274d0fe4
ACR-15a9c0c7c30f4abfb2f2ba8734b8b2e1
ACR-85ea7eeb7f0f403c988057f0ff288703
ACR-58e6bcd357e7432ca7ec555f58af75fd
ACR-af38044767c2487b996443dac48ed000
ACR-aa018d7c587942d582e88eabb44b4ecc
ACR-2c6dc4836bee4c079cef5ae074040d62
ACR-aa7bd55ab5914f5291fa1b607d2750d1
ACR-0f8151173ac8431c8c6341e89919a6cb
ACR-fea593e45f8642a8975f307cb7916683
ACR-446422bfa04241f88a4dc0479a7fd322
ACR-1c952fe0013244af935011f41e325c2d
ACR-6c931aeb0f7a4d89a85d904a43a626bd
ACR-551331524832421dbb4ff6b52fb71af4
ACR-2d600fbe164049f8bb5d1b4ffe545c82
ACR-1acfb99deec24b5b9f3bb24d4e980e6c
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.Optional;
import org.sonarsource.sonarlint.core.serverconnection.issues.LineLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.RangeLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerIssue;

public class ServerIssueMatchingAttributesMapper implements MatchingAttributesMapper<ServerIssue<?>> {

  @Override
  public String getRuleKey(ServerIssue<?> issue) {
    return issue.getRuleKey();
  }

  @Override
  public Optional<Integer> getLine(ServerIssue<?> issue) {
    if (issue instanceof LineLevelServerIssue lineLevelServerIssue) {
      return Optional.of(lineLevelServerIssue.getLine());
    }
    if (issue instanceof RangeLevelServerIssue rangeLevelServerIssue) {
      return Optional.of(rangeLevelServerIssue.getTextRange().getStartLine());
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> getTextRangeHash(ServerIssue<?> issue) {
    if (issue instanceof RangeLevelServerIssue rangeLevelServerIssue) {
      return Optional.of(rangeLevelServerIssue.getTextRange().getHash());
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> getLineHash(ServerIssue<?> issue) {
    if (issue instanceof LineLevelServerIssue lineLevelServerIssue) {
      return Optional.of(lineLevelServerIssue.getLineHash());
    }
    return Optional.empty();
  }

  @Override
  public String getMessage(ServerIssue<?> issue) {
    return issue.getMessage();
  }

  @Override
  public Optional<String> getServerIssueKey(ServerIssue<?> issue) {
    return Optional.of(issue.getKey());
  }
}
