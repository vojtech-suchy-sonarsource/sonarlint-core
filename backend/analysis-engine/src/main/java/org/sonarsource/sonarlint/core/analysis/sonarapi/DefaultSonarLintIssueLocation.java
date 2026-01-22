/*
ACR-22a7228bf2e341f9b86d178e36636411
ACR-44049f40cfdc48d68928099c9b7967ee
ACR-a21c38bc3cf840d6affc036bbd0ad278
ACR-377a591a48af4ff298132c07526e4f0b
ACR-7c3e6b8d6e524eb9a07b647fef214c68
ACR-6b967fec57b0467a91c3a1c5314b0900
ACR-dfe2bf6380cf4759826c566299da09ce
ACR-f03cd7a9fc044ed3b7f81d5c26610489
ACR-8f56da44468943b988cffd2f95f3cbb0
ACR-3e59185ae73a4d2cafe8452b2b51eed3
ACR-0f14027ecd5f4ccf9402241248bce2ac
ACR-bad2a6c73199467882cc0d2d4b66832c
ACR-ac4497a850704e349901426783bb871d
ACR-efa8e4eddcfa421c9180da76d543fa2c
ACR-9c9677167cea4b2ab3c7d677360369e8
ACR-a8f96b4e097645e9af53abb8df7755bc
ACR-e9c28d70662e48efa26014a6e02793c3
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Strings;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.issue.IssueLocation;
import org.sonar.api.batch.sensor.issue.MessageFormatting;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.batch.sensor.issue.NewMessageFormatting;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewMessageFormatting;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class DefaultSonarLintIssueLocation implements NewIssueLocation, IssueLocation {

  private InputComponent component;
  private TextRange textRange;
  private String message;

  @Override
  public DefaultSonarLintIssueLocation on(InputComponent component) {
    requireNonNull(component, "Component can't be null");
    this.component = component;
    return this;
  }

  @Override
  public DefaultSonarLintIssueLocation at(TextRange location) {
    this.textRange = location;
    return this;
  }

  @Override
  public DefaultSonarLintIssueLocation message(String message) {
    this.message = abbreviate(trimToEmpty(sanitizeNulls(message)), MESSAGE_MAX_SIZE);
    return this;
  }

  @Override
  public NewIssueLocation message(String message, List<NewMessageFormatting> newMessageFormatting) {
    //ACR-3728e24e6faa4f52a45358eb53cdd575
    return message(message);
  }

  @Override
  public NewMessageFormatting newMessageFormatting() {
    return new NoOpNewMessageFormatting();
  }

  private static String sanitizeNulls(String message) {
    return Strings.CS.replace(message, "\u0000", "[NULL]");
  }

  @Override
  public InputComponent inputComponent() {
    return this.component;
  }

  @Override
  public TextRange textRange() {
    return textRange;
  }

  @Override
  public String message() {
    return this.message;
  }

  @Override
  public List<MessageFormatting> messageFormattings() {
    return Collections.emptyList();
  }

}
