/*
ACR-31eef3eb00774727a9c9cec2e22f9b20
ACR-f29c9c8bcfeb44cc9aa81b6d43ef3fd6
ACR-5b8be0233b404281b730938f7a303853
ACR-8b3e196752a74874ad5a87d62e10be04
ACR-e23f359b6ae646fd9ebb8232019f6bc6
ACR-3b23208bf0d8440f80732a40513f4c6d
ACR-3a15063a02934c36a35c485c7ae9c725
ACR-c62c2788a4fa4a06a790dabeb89b5673
ACR-c0a5203a7cdc4f72ab686697f3d19a16
ACR-fd2280dda110438b966025f66e4fda7e
ACR-c1feb3821e854ea1af9261066132a96b
ACR-5604da4669dd42bab54742a365f1f70c
ACR-6356eefc18294d1ab12c162e5565ac2a
ACR-0e2a980dd73d47169be2f874bf28af86
ACR-b5100a06c7ea4ae0ae5a4a6a559f58ab
ACR-497e4d21fee0455c9a58f541c4f84520
ACR-976d642f40394b40b9311b03f61c7d8c
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
    //ACR-fe0d2c3607254a43bf7a2ca8ebc99d97
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
