/*
ACR-6627d6158c4a48afa1903aa0bec7992c
ACR-1011b12dbfe3432a9b69c08ac80c653b
ACR-c1e0b90f279f4b05aaa2b67ebf4da942
ACR-57801ef17a444110936c01dc5d3c4df8
ACR-97a686487d7d4a348c97937dd86026be
ACR-9743f8a1e1b54a3ab6feeafa72efa6a0
ACR-40c0a662670b45deb8fff46678dcc07e
ACR-b0a7739d0a3a4730b0a87ae6912ea5f1
ACR-1a85e149857a4bff8205e8306561952a
ACR-d0949a8bb1774c1791edc0bc578c4ca0
ACR-128d819cf44049478d718f6007f1682e
ACR-bed6eadd84d44c8e896cbc7938eb1642
ACR-36e856875fd347789cf01bb83600973f
ACR-0c09c5183630423ba104667ebc670cde
ACR-4070f2ec471240b1bda06f22867c7d8f
ACR-e1e0308d2b2a4d10a0eef8f0586c92e8
ACR-e9210fc860184132b073972b7e45e79a
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
