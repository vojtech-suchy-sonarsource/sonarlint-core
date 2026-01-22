/*
ACR-4269d1845b2c4e3e9d204fe054688124
ACR-3a1f00de3f6943bb936ba3b02237e657
ACR-6299bf73f84c48d4a9506f86592dbeae
ACR-6af94f60271c4a698c1c9de94c550033
ACR-d3702e51e6f043549350a843bf75055f
ACR-afaf2ecc4ff34810b5bcd00fe4f72fc8
ACR-ee393ad15fdb4976b65d869166f36eb3
ACR-45774e0b6f424f03bdc37f6364029baf
ACR-de2167d6dfad41d1a627adadc6cdea8d
ACR-eb2d7b0c27d84c15a627e1bcbeefea4a
ACR-a61b1512f0b845e89024319b3fbd8a76
ACR-531b1320660847a2b4f0e03f08cc392f
ACR-205df713912044f7b2d2c08f71c710bd
ACR-186dd9977c824e8994a8229ba1f11ec4
ACR-7e11cf81d33f45d0b3733dedf1803d1c
ACR-cc5384ac8e2d47979773aeba12b1e505
ACR-32d01e89098a417cb597b61879b3911a
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
