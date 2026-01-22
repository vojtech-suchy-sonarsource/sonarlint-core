/*
ACR-b030a2c8efee4983810087f7af71c68b
ACR-79fa5312c1f54b67847c4c15440835e8
ACR-8362af04726c4072b6b81417649b1bba
ACR-aed27b1c2f544a5ab793b9cbff84c4ea
ACR-6ea3ecc11d574514a588d1fd220b37af
ACR-1cb6469954d742d386e203c69b576ef1
ACR-764075f21758447e8434cbcea613f09a
ACR-1a1dd29a51894f9d81a30fcb2b538838
ACR-bfbdec8818974398858826b578035d0b
ACR-e8a3e1ff9cc84b319c38390617047042
ACR-f44770f1cac745068333ee28af4c3927
ACR-a3d3fa86c5ed424a89bcc4d69c2870f3
ACR-4e570045deb34910bb1e83bd96050b08
ACR-34c7f2f8422c4e0b891ea063077dfc09
ACR-866b5012a25940c7b539c10f9c31cd20
ACR-6d1fdfaa13d240009c7b11ec46c3a73f
ACR-0893b69fe3da4d6fa12391589a736244
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
