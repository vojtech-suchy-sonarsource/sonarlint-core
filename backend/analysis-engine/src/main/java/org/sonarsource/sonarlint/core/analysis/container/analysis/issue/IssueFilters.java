/*
ACR-f942b2dfacab4978877ce28d9f22b7a5
ACR-0fcb363612eb4137b0214c9767659ea2
ACR-82a44bdf16324da1b22b28288a10784a
ACR-07539346e4fd4c3ebef3fed1a3dd2e5f
ACR-d673c94b07ec4854b024b870425e9bf1
ACR-9ff0a6e19f8642e396f7c352efabf0cf
ACR-872bf4b473c44c65919c215abe6ccb7e
ACR-d53d8206a5c1419d902f454d28c46976
ACR-8b924cb0fc624cd3b841e3dfe96db345
ACR-5c6e420a54f0451395f25f0269857481
ACR-ca7e2fb022934242b44913c10010fb91
ACR-ee0e1b78a3184b28a9bc329942358de7
ACR-27c96539c15a42c3bdabfaace6adb7ba
ACR-cc4ae0582d994c7eac880b6f024d89bd
ACR-9b08d0019afa495ea2cb5b23fd289d34
ACR-ea99b13c4ec74794ad7d4e609227ef0a
ACR-021f253c80f94aa69892cda80907554a
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue;

import java.util.List;
import java.util.Optional;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.scan.issue.filter.FilterableIssue;
import org.sonar.api.scan.issue.filter.IssueFilter;
import org.sonar.api.scan.issue.filter.IssueFilterChain;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.analysis.api.Issue;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultFilterableIssue;

@SonarLintSide
public class IssueFilters {
  private final List<IssueFilter> filters;

  public IssueFilters(Optional<List<IssueFilter>> exclusionFilters) {
    this.filters = exclusionFilters.orElse(List.of());
  }

  public boolean accept(InputComponent inputComponent, Issue rawIssue) {
    IssueFilterChain filterChain = new DefaultIssueFilterChain(filters);
    FilterableIssue fIssue = new DefaultFilterableIssue(rawIssue, inputComponent);
    return filterChain.accept(fIssue);
  }

}
