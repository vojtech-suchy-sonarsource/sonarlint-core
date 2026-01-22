/*
ACR-6c9e8294fff14bf6b1a157fa8d01664f
ACR-c51c43d7cbfe43fba2455c5f397320d9
ACR-cf434785cd40447f9f6d73068a6208f8
ACR-5253fb45250a4ec780fd4429a2a0e343
ACR-859d63c1eee84495b75e2aa861df4592
ACR-7e09322c38594796b1af8ce023de903f
ACR-a174d2476b59423e9747792379e8a102
ACR-71e329223f1442ecbaaf8c44d02a2e86
ACR-473db39fc3dc4cad90a4f18a4c77edb2
ACR-07385bba2aa1464aa946d3dcfacb96ce
ACR-688af85c8a034262a6116d7b91145b17
ACR-4938247ca4d1477fa6cec25005203e96
ACR-a50a7c84aaef43d2a17ef656358b33f5
ACR-030dbec362014591b3a6b3e1ebf05d85
ACR-9bfa6c301f094f06ac2236d90a200f1d
ACR-01130d7fc3814a769df1236f0df86ea2
ACR-7aa7709c5ae14429b2ccc5d514f58099
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

public class LocalOnlyIssueMatchingAttributesMapper implements MatchingAttributesMapper<LocalOnlyIssue> {

  @Override
  public String getRuleKey(LocalOnlyIssue issue) {
    return issue.getRuleKey();
  }

  @Override
  public Optional<Integer> getLine(LocalOnlyIssue issue) {
    return Optional.ofNullable(issue.getLineWithHash()).map(LineWithHash::getNumber);
  }

  @Override
  public Optional<String> getTextRangeHash(LocalOnlyIssue issue) {
    return Optional.ofNullable(issue.getTextRangeWithHash()).map(TextRangeWithHash::getHash);
  }

  @Override
  public Optional<String> getLineHash(LocalOnlyIssue issue) {
    return Optional.ofNullable(issue.getLineWithHash()).map(LineWithHash::getHash);
  }

  @Override
  public String getMessage(LocalOnlyIssue issue) {
    return issue.getMessage();
  }

  @Override
  public Optional<String> getServerIssueKey(LocalOnlyIssue issue) {
    return Optional.empty();
  }
}
