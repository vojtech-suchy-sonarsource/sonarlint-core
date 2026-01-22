/*
ACR-43b7d4139d0a426fa66c0b465ac12a66
ACR-9d7108fa31ba44b4903931fa0596b274
ACR-76bff93a340d4176bdfe3785cd5bbc38
ACR-bed151eaa01d40bdad315d16ed87f344
ACR-7ee84de3a60b463bbfce60f9a0bcca5a
ACR-afa5af0093d94d1eb0a686c80d175555
ACR-e1ff5ca4fbec4acab3c599392b89004b
ACR-32b4a2eec2af4c4c90d565794317851c
ACR-d18632c29d6242599fae230977715939
ACR-fa3e877061a44c519f39067751f81397
ACR-96509f8384574b4783c63b32bf4e6173
ACR-e7c7a8b3a7f147d68b9ce85457f9687e
ACR-44f90cf3a5fc48d588035dd66ac1821e
ACR-481897c731ef400b8ae20cf0c8f6579b
ACR-831a27f47f9d4aac9133b7ce1de00cf4
ACR-c7274f79726345afa78adb19b868b0fe
ACR-4ebdfa2f0b0240ea9548bb3f3261259c
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.Optional;
import org.sonarsource.sonarlint.core.tracking.TrackedIssue;

public class TrackedIssueFindingMatchingAttributeMapper implements MatchingAttributesMapper<TrackedIssue> {

  @Override
  public String getRuleKey(TrackedIssue issue) {
    return issue.getRuleKey();
  }

  @Override
  public Optional<Integer> getLine(TrackedIssue issue) {
    var textRange = issue.getTextRangeWithHash();
    if (textRange == null) {
      return Optional.empty();
    }
    return Optional.of(textRange.getStartLine());
  }

  @Override
  public Optional<String> getTextRangeHash(TrackedIssue issue) {
    var textRange = issue.getTextRangeWithHash();
    if (textRange == null) {
      return Optional.empty();
    }
    return Optional.of(textRange.getHash());
  }

  @Override
  public Optional<String> getLineHash(TrackedIssue issue) {
    var lineWithHash = issue.getLineWithHash();
    if (lineWithHash != null) {
      return Optional.of(lineWithHash.getHash());
    }
    return Optional.empty();
  }

  @Override
  public String getMessage(TrackedIssue issue) {
    return issue.getMessage();
  }

  @Override
  public Optional<String> getServerIssueKey(TrackedIssue issue) {
    return Optional.ofNullable(issue.getServerKey());
  }
}
