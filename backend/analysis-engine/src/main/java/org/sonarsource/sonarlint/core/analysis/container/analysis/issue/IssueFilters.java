/*
ACR-ec2744072ebe4cf48e80a8a984eb5cbe
ACR-0dfef2f179c847cc8f21b9cab4c81e62
ACR-49758a1f9e6a4da9af62ce6e9f245d07
ACR-1c084aabd1074f6192410d2cd8b4ee7f
ACR-3760be79f0ad472cb304684c9c148f07
ACR-680beadc1d174e96bbc41b5c47afe5e8
ACR-cea2c34afcb94904bd79a2b404294e63
ACR-46d13f8d5bb94891b6df8936e39ebd54
ACR-eb9840162bff44c4afee514ab347ae81
ACR-bfc295300894494badc88be0dbcca7f6
ACR-ececc87db8814027b6d9adfaaf4d6d6a
ACR-a64e460ff576486aad297c12ef9404d4
ACR-362d6c22b7e5479f95b1e794f32c2341
ACR-62bd352cd1074aaf87f5bbd82ecaaef2
ACR-14ebca54c3834aba9a1b96d8d801acb2
ACR-f084045f98344091a301b6a25858ec4b
ACR-0e230a2005d24282b8ab37feb29d3494
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
