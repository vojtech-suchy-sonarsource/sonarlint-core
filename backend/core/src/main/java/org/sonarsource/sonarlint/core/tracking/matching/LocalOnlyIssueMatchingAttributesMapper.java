/*
ACR-833b03f4d1f14e0481e5e7d2365285d7
ACR-a57683c5035044e89948a3852bef207b
ACR-4d50e555ca5441b89ffafc899c436879
ACR-bae400646541403b8d14a2455b0beecd
ACR-e02d95dc6b434f5dbcbbd390410030bb
ACR-24019f2b2ca04123a2ab5fcb3e4f4b10
ACR-0a7b7011d33c43f78f864336b7be58c7
ACR-5a2ed5d3a81647de8eb19cba6a400709
ACR-d0da084dfae24babbcf9aedfb2eeda99
ACR-1cf8a33a81f54fce98cf3bfbc229d057
ACR-5a3100a97adf4b3c8681efa46f720acb
ACR-afa80cd1375f4df695a73b9b893845be
ACR-e049be0c285f433088c9075801fdafbe
ACR-b0b80e20714a43b49617c9a929849762
ACR-afe9e4ea7b2c4da089d11f10407c2ede
ACR-c73cbe23963644ccbb704d545eb1135a
ACR-f885802274ae4aa0af54a15329258acf
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
