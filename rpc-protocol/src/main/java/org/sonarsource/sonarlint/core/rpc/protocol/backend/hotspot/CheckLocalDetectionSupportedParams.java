/*
ACR-a200cf2a03c2404e8a0be3590855f693
ACR-bb7efcf366d5448b9227d4f28afdeb56
ACR-2055627fa45749d8bc278370a3c904a1
ACR-271aafbb0db84b41bbe98ce6fc89c17a
ACR-c5eaa6ea7454405196065f573eebc6dd
ACR-1262b2ed13bc4d4d9c56875ddc488ca8
ACR-234ea84137d8438ca21d2d342a7ec49d
ACR-b7a9b59d90c64b3293237e603b572604
ACR-268944dcc86649f28921ed1f8ef3107b
ACR-2b5bf139b68440d8aeb025faee87ea12
ACR-802ab7f495a14d9f9f0726870f88ef17
ACR-90ed62c5eec1420c99bfaca8bdf3f88d
ACR-9f87e794cb5e466188a1017c3990b87f
ACR-3981270d40624f898cc38e30a1b50b74
ACR-aa0cab46704e4a2c8c187f0919684fe0
ACR-1dfe433cc33e415b9dc94fb23887326e
ACR-d2c4ece0bc91413f8028840bef290f46
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

public class CheckLocalDetectionSupportedParams {
  private final String configScopeId;

  public CheckLocalDetectionSupportedParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
