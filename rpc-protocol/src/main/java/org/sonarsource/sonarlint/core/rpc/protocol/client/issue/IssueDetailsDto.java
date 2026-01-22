/*
ACR-3be7509278db48bca548f647af7fed81
ACR-f4f7136b00e6455bac1fc84342889eea
ACR-315ed4eafacc48118476aa06a4709819
ACR-79a3231d968b42b983011e35bb1e6d9c
ACR-8bc62cb69e4e4abd9384c093a454cc32
ACR-6e9d56b27c164f1aa02d78ef16af1bdc
ACR-0e9ee14d2f5e4cccad8dd1608c6fb8e2
ACR-d54d090871b34855bfb95a72f6b6d960
ACR-0d21ffbfd27a4cb888aa00200564376e
ACR-bcd5a34cc0554c9d918d8df50a5787eb
ACR-997ee8ef58534a55a90191e706157ad5
ACR-28c8b10963d542c1a1e265010b40e2d6
ACR-3ed1e5c9f1e842d69a09c147838cb68e
ACR-c7f9e575751442f4903b12dae15bada2
ACR-24ee96f64efb461cbf399faa2004e666
ACR-ddb8cc92e74f4c3bb47be2241b4e5dbe
ACR-8d3de8c6da5d47fda120713eb4a75eac
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
