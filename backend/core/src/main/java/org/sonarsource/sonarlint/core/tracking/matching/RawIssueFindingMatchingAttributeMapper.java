/*
ACR-d903193cdcf94e9c9a7b57d43c058c44
ACR-aac247a301474c8da0556d36fff41418
ACR-badbc26c18f24ec6831d8c449a50e03c
ACR-7e7a6f52957d4718b3a793705721892a
ACR-a74468efa9944ad6b90a9b2fc2727b81
ACR-f4a5842c71ee4380a4a1fbae7c03e62d
ACR-b2866c48695b42ee9115fc1161bfed67
ACR-c6ed7c1e2d3e49759ad4f3c7ffe7848e
ACR-69ebda30c5d74f42a988f878d221b397
ACR-7ddace729844423aa19a2ed153273261
ACR-a6990931c6cd4652a4433f40fd83913b
ACR-5ada7276e8e24e0ba19abad6d896c5b3
ACR-73b3bf5957c042558dfc609c038d1c0b
ACR-7359be86d6344da28ad95baaead8511e
ACR-0a69f328813041f5914490b403f638c7
ACR-fb38f43f7e1e4e2ebb95ffe001f9007a
ACR-855343a788aa470782d3c65f1a0f69d4
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
