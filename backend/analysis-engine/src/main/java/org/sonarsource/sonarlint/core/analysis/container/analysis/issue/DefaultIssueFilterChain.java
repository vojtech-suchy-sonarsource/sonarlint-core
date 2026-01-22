/*
ACR-13e7a93120604956a8808ca548fd8bc7
ACR-62219e6ce7b54e69b430126701ee44a0
ACR-f45cab2f57cb46438ba2735ccf6213b9
ACR-dcc24316688e4609bbf0a35f03f7dbe7
ACR-0bfdf77f9eff43a7a4ca0fdacda65bd0
ACR-2abd4320b4ec4c55943ce306bf94c190
ACR-51fce76ca4bd45eb8def3a8f9e1546cd
ACR-c278eb508e764f26976cf5f5c056725e
ACR-64d44aad839148d58de8c4a72f178c76
ACR-07b5d9edaec342cb83f237b829f476c0
ACR-ca85d65781e94652a1a35d2f2d63ffa2
ACR-e7a5dc7bee6c4aea81dda7a073bed280
ACR-6e3a9a089eae47379cac90250a74a3c2
ACR-6b33a19a7da54646a1546e0384d7b4da
ACR-372bd355401e4b5583d8587831208b37
ACR-0aceede79ebb490483d4e18f39ef2281
ACR-5558af6cee164b7f8273471a1fcb87bb
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue;

import java.util.List;
import org.sonar.api.scan.issue.filter.FilterableIssue;
import org.sonar.api.scan.issue.filter.IssueFilter;
import org.sonar.api.scan.issue.filter.IssueFilterChain;

public class DefaultIssueFilterChain implements IssueFilterChain {
  private final List<IssueFilter> filters;

  public DefaultIssueFilterChain(List<IssueFilter> filters) {
    this.filters = filters;
  }

  @Override
  public boolean accept(FilterableIssue issue) {
    if (filters.isEmpty()) {
      return true;
    } else {
      return filters.get(0).accept(issue, new DefaultIssueFilterChain(filters.subList(1, filters.size())));
    }
  }

}
