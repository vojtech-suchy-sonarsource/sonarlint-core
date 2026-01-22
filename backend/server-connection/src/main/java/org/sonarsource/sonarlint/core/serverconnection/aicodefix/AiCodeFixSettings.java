/*
ACR-898356b434aa4cc4970ab37f870c710d
ACR-ec734e5d25d7481a9398a08aa85b27a7
ACR-b70b6e9aeaf644bd86753023fc828df1
ACR-c09f5673bfa94f32a75744c70f60569b
ACR-ea79f3b62cfe42648fd97a2a020555ef
ACR-1d518173dcc947ec9fe5d3135bc61faa
ACR-52fb91292ff443a2b2e3eca1577ace19
ACR-31f39e126f6e41c8be0ff72f62c00f29
ACR-68461e7870e84df0a745f0ed53323ca3
ACR-c715dfac71f34145b4b45c5fd403e145
ACR-c0050c5334fe44ff8b3f34f4fc97b9b2
ACR-81bf2f6828b64ee19b667b2fec71682b
ACR-9f4ead8243254e99a81e9a9333b93e8e
ACR-6379cdfea7ca407cbc0fe51c87e54c49
ACR-97ce3ccf0d43432a89e29ddbd1e2f4fb
ACR-0129ff14d614464b85363c50659dc0fe
ACR-460d5b2693da44e18ecd232c83648bc3
 */
package org.sonarsource.sonarlint.core.serverconnection.aicodefix;

import java.util.Set;

public record AiCodeFixSettings(Set<String> supportedRules, boolean isOrganizationEligible, AiCodeFixFeatureEnablement enablement, Set<String> enabledProjectKeys) {
  public boolean isFeatureEnabled(String projectKey) {
    return isOrganizationEligible && (enablement.equals(AiCodeFixFeatureEnablement.ENABLED_FOR_ALL_PROJECTS)
      || (enablement.equals(AiCodeFixFeatureEnablement.ENABLED_FOR_SOME_PROJECTS) && enabledProjectKeys.contains(projectKey)));
  }
}
