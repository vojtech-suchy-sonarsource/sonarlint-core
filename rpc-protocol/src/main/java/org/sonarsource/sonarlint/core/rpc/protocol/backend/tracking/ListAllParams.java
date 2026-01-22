/*
ACR-9e909af53b10475f8fb7fec9ef28ce14
ACR-cafc3afacb1f47a7854d99b2bdafb4f9
ACR-ff99491d2a31417baa7aaf9b893a5875
ACR-759f178ade2f4246ad66f6ceea7ae683
ACR-43d0e847dd2c4eaeb4ac1681b2c23473
ACR-5169c1e3fc18436498e37aced28ba398
ACR-9687d4a53b264abeb5a1fdfce304e775
ACR-5b54e8f066f44e77ab22e624a36d282e
ACR-da45b836620646919fa6d3d4c0d6cf8a
ACR-0beea527bb844e3e95bf4fb4fde5437c
ACR-e342bb5ad0ed437ebfa6ec57304569a6
ACR-604e64a5b875429da223b3be42255a88
ACR-0c5c4b43b133464fafac9aef5eeec562
ACR-f5b5cd4df2ea430fb13633e0f65bde34
ACR-964aacb7205542f79d142daf0b08cab0
ACR-86c5ace0f58a44a992215a5d7ac54c4a
ACR-6b82fbcc1897449fb2d2a888a491f1ca
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

public class ListAllParams {
  private final String configurationScopeId;
  private final boolean shouldRefresh;

  public ListAllParams(String configurationScopeId) {
    this(configurationScopeId, false);
  }

  public ListAllParams(String configurationScopeId, boolean shouldRefresh) {
    this.configurationScopeId = configurationScopeId;
    this.shouldRefresh = shouldRefresh;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public boolean shouldRefresh() {
    return shouldRefresh;
  }
}
