/*
ACR-71792d1909c04b228559c7e6c8a44d8a
ACR-455845f23907451883a296da43e4a785
ACR-a2345aea91634b9ba4773c6de25729c5
ACR-bd98f183ab7048c5b68ead7c736e56d9
ACR-e45b469fc8f54f83b02bea1f58e97f32
ACR-534969b5a42f45cab407d27d47c4c9f3
ACR-5153c880858c4cca80cae269da831ce1
ACR-07495c15361046269e6a43e24ae536ef
ACR-8e4d7264f8df4654a75834d23f61c6cf
ACR-822501db90374021b0f7d88b9d8731d4
ACR-25d5a80edab943a6bd7361101e99a078
ACR-1ae31ccf41504603939580740684ba66
ACR-a6a2fe5dc8ed4ad288fa6b53feb35fc0
ACR-d9e5dd5f94b34058aa5ac1e846a3c3b3
ACR-388efcf1f78748828222b6534fcbb02d
ACR-458f630270014a2ca17a569067d5e72f
ACR-a18fc3953dd74f49a932f9475dfe87a7
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
