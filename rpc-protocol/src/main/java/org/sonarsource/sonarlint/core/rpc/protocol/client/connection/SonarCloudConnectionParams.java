/*
ACR-44f85ba59f7c487a8bb6c91da650206d
ACR-37091b3c969840889fb4853254048224
ACR-a2f0b5597e5f4467ac8579b07419a67a
ACR-ab0800d02f5b49afaddd6b05f8b02f98
ACR-6c646b4dc3014a95a88d1bd68ae92ecc
ACR-02448f832dc9462fa4a04ecf533804b0
ACR-5d65ed7e2fe44e61861053e1188a53a7
ACR-7f8699c26c8a40f28bd1f4ad56212984
ACR-e3316126d1814267be76b09978444bef
ACR-fecde584016f408085ec459ad1908d32
ACR-200268cee6d24beebabe6f113354a377
ACR-74a88ce825d5466f8f0b1e8e1d39ac4a
ACR-399a394ae69048f1973c75f605c7672d
ACR-dfd474a2619d4c839094d4bf53b23e03
ACR-c41a02e5b1694e7dbc28241cd94f85be
ACR-9e55e14918b54b729ca63ee4f1911e88
ACR-a5b57be587d3460188ae6fb708438c0a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;

public class SonarCloudConnectionParams {
  private final String organizationKey;
  private final String tokenName;
  private final String tokenValue;
  private final SonarCloudRegion region;

  public SonarCloudConnectionParams(String organizationKey, @Nullable String tokenName, @Nullable String tokenValue, SonarCloudRegion region) {
    this.organizationKey = organizationKey;
    this.tokenName = tokenName;
    this.tokenValue = tokenValue;
    this.region = region;
  }

  public String getOrganizationKey() {
    return organizationKey;
  }

  @CheckForNull
  public String getTokenName() {
    return tokenName;
  }

  @CheckForNull
  public String getTokenValue() {
    return tokenValue;
  }

  public SonarCloudRegion getRegion() {
    return region;
  }
}
