/*
ACR-c2095f34cd1e472cac6c4e4ca44dacbb
ACR-e1178c5ce261493db4cf519f9102381c
ACR-4db778d861fa48bdbac6a9e685710eca
ACR-c0efaabede874620a95c36607241fd62
ACR-4e271cd0785c441dbe34b692bd5a1a17
ACR-3fe98f06ed8b4c7f82ec2aed291f0c8a
ACR-c0440beda2d142e0a7b679242fd719f2
ACR-770ed8cd022243b5b95773d66c6d30a4
ACR-dd61999cfeaf42309da9e656a79174ce
ACR-3dc067431c6040beb5cb7642d5549113
ACR-3bcab08ef78842c380be854b04fecae7
ACR-e3d1a85299ed48ea97b88b14732a4ef9
ACR-466926a1035e424dae9960be94ac6849
ACR-18256628d80e48cdab6dab9b865da8ff
ACR-d53bfd9028184b5d8db560be90ecda94
ACR-4b17eb56abe24d62b1f0facefe75f30b
ACR-40c9c97f29674a468cd0be37a1380c6b
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
