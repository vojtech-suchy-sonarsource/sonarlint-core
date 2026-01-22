/*
ACR-66ecbcce7e4b49358597da6b16a2571c
ACR-d137962f3aa54ccf8253615011a33894
ACR-50500ba17dbb4ca9bd94ad646232c360
ACR-2dd9cb1c0d6241588b6a2ab45ad10e48
ACR-6297dbfc040c4c338b2d0de8212df81b
ACR-ed5105238fd549f9adbdc6d6cb021adb
ACR-bfca85fc0cae4e7d90c99ea7f5b4f62e
ACR-12973cf19e374069b6c4f44cf765ccc8
ACR-81abc200f66e4e1ba7626edf70e99c9c
ACR-2b002e5a49444fca954c5a5a0ede9c8b
ACR-e99033ae6953412582d2546b00c46e2b
ACR-43ec2d04685a4b5ca2331c479ad7a5e6
ACR-9c7a7667162e46468bf96afabcba09b0
ACR-204283da94e04086be24fc8db865b8af
ACR-93afacabff1b48228900dfe42d2961a7
ACR-27ded36354b3480bbaad92f7720edd46
ACR-6607abb16af94ddab08755ab0f106627
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config;

import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;

public class SonarCloudConnectionConfigurationDto {

  /*ACR-5d28b316fb9d4c8c9b81866ff277e095
ACR-7eb395103efb4fb5bcfe27a4f50e9b45
   */
  private final String connectionId;
  private final String organization;
  private final SonarCloudRegion region;
  private final boolean disableNotifications;

  public SonarCloudConnectionConfigurationDto(String connectionId, String organization, SonarCloudRegion region, boolean disableNotifications) {
    this.connectionId = connectionId;
    this.organization = organization;
    this.region = region;
    this.disableNotifications = disableNotifications;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getOrganization() {
    return organization;
  }

  public SonarCloudRegion getRegion() {
    return region;
  }

  public boolean isDisableNotifications() {
    return disableNotifications;
  }
}
