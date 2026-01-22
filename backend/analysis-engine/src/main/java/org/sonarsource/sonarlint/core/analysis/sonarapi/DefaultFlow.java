/*
ACR-fd3c5347f98a45258db5064867547525
ACR-87d460fe973142cda14a4f3348f76d5f
ACR-2efb84e6613649d39af8c697e952a643
ACR-4081dbecb9e94d98b77f6468082248fb
ACR-3ec3800ab0ec4fd3b85ad28c0db8ef49
ACR-be06eacfea254b2b8d35ab9557257ea4
ACR-f3ec8686623c4edcac45ecdaf7ebb57c
ACR-e769597cc160420d9a549a2a09c5a091
ACR-ed93c73741db4ab49d9ece44793e6f9f
ACR-38f26f1013244672b9772800ded1ac10
ACR-d3fd7a92628040b0a2905ea59723b6cc
ACR-62e88ea638e047e29bee6e4dda57ed82
ACR-7f1202783bee4eee9d3c0a2ac8dc6d47
ACR-7bef3c820d0847baa7c0ff6a8523bb2d
ACR-2e6a2ecdaeed49eda0a0d4776a7b49ea
ACR-131dc4b7224149c08a28597e696e49c8
ACR-7b43b879c98a45bc9c896a0fe65a2b05
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.issue.IssueLocation;
import org.sonar.api.batch.sensor.issue.NewIssue;

public class DefaultFlow implements Issue.Flow {
  private final List<IssueLocation> locations;
  private final String description;
  private final NewIssue.FlowType type;

  public DefaultFlow(List<IssueLocation> locations, @Nullable String description, NewIssue.FlowType type) {
    this.locations = locations;
    this.description = description;
    this.type = type;
  }

  @Override
  public List<IssueLocation> locations() {
    return locations;
  }

  @CheckForNull
  @Override
  public String description() {
    return description;
  }

  @Override
  public NewIssue.FlowType type() {
    return type;
  }
}
