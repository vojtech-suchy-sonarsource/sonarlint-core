/*
ACR-e05c6a399e3045f2af19cfd4c6fa9e62
ACR-0d14c619b602489e95d7db5c7de61b7a
ACR-27ec109801aa4c4493239631edf80388
ACR-1399012b8d414253a99d291a7dd6b249
ACR-dd3ddeff10bb43f59fa09807a54e8754
ACR-53b9be0c6f9244cca9582f27c98ba837
ACR-49fd30a7fbca42d78b7bbef0da4ee6c5
ACR-f1375043f56b4ea18e569f4aaf20163e
ACR-0b08716254ad4718944750405f8503ab
ACR-262446f913a64d358da4de97744f3249
ACR-af9fd8b56fd74a0ea4579cba629aab59
ACR-52d082f7b6f2455f8ffae3c94f5c6afe
ACR-76c764e49e604f2db9c1df266812681a
ACR-0aee5c1da9154bafb64611f44190711b
ACR-02126ab99b7346ca83af3f9df01a3a83
ACR-df83c0f1b48748b095be278d1cdb06b2
ACR-47b8db51ea074a92833dc6fac87bb3e8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.binding;

public class NoBindingSuggestionFoundParams {

  private final String projectKey;
  private final boolean isSonarCloud;

  public NoBindingSuggestionFoundParams(String projectKey, boolean isSonarCloud) {
    this.projectKey = projectKey;
    this.isSonarCloud = isSonarCloud;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public boolean isSonarCloud() {
    return isSonarCloud;
  }
}
