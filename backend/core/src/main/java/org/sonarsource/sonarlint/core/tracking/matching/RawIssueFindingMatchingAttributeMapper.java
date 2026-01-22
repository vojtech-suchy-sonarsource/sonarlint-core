/*
ACR-9c2b10c2877c495c8a136a47fbbf5132
ACR-1bc5f593a4fc4715a4152fd3d4aeeae0
ACR-489eade4906a43b8812ee59cd49d5fbd
ACR-7d3fb38a96e346c8a809be3cc8a97bf1
ACR-6998560991314a85b9cd3d3579235973
ACR-20589e2956654fa1b2db50a2f4b3b4f2
ACR-3555d7a8800149d9a1143675ee8142e7
ACR-6f685a988a584f8bbb144933c8496c67
ACR-7cb8c9ad8a1c452999b80a438a57fc8d
ACR-2f15a83650ab4a5cb756f4bf5892bc54
ACR-ce478bf32ef9408d8a3c2a2f58b2aede
ACR-379e2a40a7154473854ec7bb590c6900
ACR-08040179120449dd939e3d14cf796a66
ACR-d53a66af510d4d1e817f6a2a4c3d01b5
ACR-720d358965a7455e991e07816b3d6ae0
ACR-7ad615d1d72c4658b4b9743281ce28a4
ACR-8ecc25d1cfec4bc08c16b0cf994c6207
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.Optional;
import org.sonarsource.sonarlint.core.analysis.RawIssue;

public class RawIssueFindingMatchingAttributeMapper implements MatchingAttributesMapper<RawIssue> {

  @Override
  public String getRuleKey(RawIssue issue) {
    return issue.getRuleKey();
  }

  @Override
  public Optional<Integer> getLine(RawIssue issue) {
    return issue.getLine();
  }

  @Override
  public Optional<String> getTextRangeHash(RawIssue issue) {
    return issue.getTextRangeHash();
  }

  @Override
  public Optional<String> getLineHash(RawIssue issue) {
    return issue.getLineHash();
  }

  @Override
  public String getMessage(RawIssue issue) {
    return issue.getMessage();
  }

  @Override
  public Optional<String> getServerIssueKey(RawIssue issue) {
    return Optional.empty();
  }
}
