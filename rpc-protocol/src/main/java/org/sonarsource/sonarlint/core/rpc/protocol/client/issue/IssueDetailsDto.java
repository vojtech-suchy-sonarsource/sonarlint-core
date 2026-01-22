/*
ACR-fefb9f61eb464952bbab9f177d0184f8
ACR-412036ae88cf4b0e9a0e397cf9d6f60d
ACR-f48a2d836ad341b288252adf88cffa59
ACR-5454fed63bf143648b2ae3669f3a66b5
ACR-481aacd251f4405eb7ff6febb86448ba
ACR-0476a00c270343b3b51c7c9060fd6b64
ACR-83ea3a770cf148b5a5136983cb1343d6
ACR-e2eec9c45e72419baff1d75ecf1fc1d5
ACR-67ef778148ff44d094b0a75b583c3584
ACR-9b56357268a940e7a7543808300ec83c
ACR-eabcac44101d4f489d109088941fc9d8
ACR-b1af650d06ea44d1917ad04fcd85c983
ACR-e1ef2f1e0d814b4b9c7a79e6cf572a82
ACR-15b8d0321f7f4278ab0ac6dc9643a5e0
ACR-b439b2cc32734d31a27c173d5684d4f8
ACR-2a3e7d3e3bb242f19d93c0d2e2694201
ACR-f925830d75654dedae7e1c48202c6a62
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.nio.file.Path;
import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.common.FlowDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

public class IssueDetailsDto {
  private final String issueKey;
  private final String ruleKey;
  private final Path ideFilePath;
  private final String message;
  private final String creationDate;
  private final String codeSnippet;
  private final boolean isTaint;
  private final List<FlowDto> flows;
  private final TextRangeDto textRange;

  public IssueDetailsDto(TextRangeDto textRange, String ruleKey, String issueKey,
    Path ideFilePath, String message, String creationDate,
    String codeSnippet, boolean isTaint, List<FlowDto> flows) {
    this.issueKey = issueKey;
    this.ruleKey = ruleKey;
    this.textRange = textRange;
    this.ideFilePath = ideFilePath;
    this.message = message;
    this.creationDate = creationDate;
    this.codeSnippet = codeSnippet;
    this.isTaint = isTaint;
    this.flows = flows;
  }

  public TextRangeDto getTextRange() {
    return textRange;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getIssueKey() {
    return issueKey;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public Path getIdeFilePath() {
    return ideFilePath;
  }

  public String getCodeSnippet() {
    return codeSnippet;
  }

  public String getMessage() {
    return message;
  }

  public boolean isTaint() {
    return isTaint;
  }

  public List<FlowDto> getFlows() {
    return flows;
  }
}
