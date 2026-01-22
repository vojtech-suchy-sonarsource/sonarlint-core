/*
ACR-e0ceb69abe704b43bdf8779d9a837b8e
ACR-4cdc18831d774de3baf93a9fa1f6eb11
ACR-0183b514d2594d159aaa0ea96137048e
ACR-0066b6604f6f46ceba08d3b2e0b3af6c
ACR-fe7cf8c488c14294add768d3d7a61d88
ACR-d4769f4ab87d4f05b0a3827296154d70
ACR-63d4620ade9f48c0987462143124858c
ACR-fc06e75b8d0249b396893d7df2b95110
ACR-e8b3047c86ed4f6d882cab915c028f6c
ACR-620406e66c374e4a8fa2bbd7ec3a591f
ACR-d13cceb3ef8d4677ac042e8e19abb3e9
ACR-2d24c7979e394cc38dd353e312e6ef4e
ACR-adeb1881086942889c964a6a87609fed
ACR-a62b49f4f3fc4ad1bedba3b32df60b6b
ACR-cd4aac40949f4c5688af937191f8759f
ACR-ad157dd605924d58bd2c932445278b7a
ACR-7c52a92907cd43cf83ea6e20b396f604
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config;

import java.util.List;

public class DidUpdateConnectionsParams {

  private final List<SonarQubeConnectionConfigurationDto> sonarQubeConnections;
  private final List<SonarCloudConnectionConfigurationDto> sonarCloudConnections;

  public DidUpdateConnectionsParams(List<SonarQubeConnectionConfigurationDto> sonarQubeConnections, List<SonarCloudConnectionConfigurationDto> sonarCloudConnections) {
    this.sonarQubeConnections = sonarQubeConnections;
    this.sonarCloudConnections = sonarCloudConnections;
  }

  public List<SonarQubeConnectionConfigurationDto> getSonarQubeConnections() {
    return sonarQubeConnections != null ? sonarQubeConnections : List.of();
  }

  public List<SonarCloudConnectionConfigurationDto> getSonarCloudConnections() {
    return sonarCloudConnections != null ? sonarCloudConnections : List.of();
  }
}
