/*
ACR-80527fa0696f4a97a1f46e7a90c35e9f
ACR-b4610ee5450b4821a316dbc21e8db75a
ACR-0da87e600d114734ae67b3ac831d9789
ACR-3497724f26a347fa82d6d7293ebd2c17
ACR-5868585ca46c48dca87842114f1d921c
ACR-cfe53a941be840fba5fa2b71d87b5213
ACR-fd194f1f9c83431a861df60c15c26a52
ACR-ed3a15f720764864b4bedb7a3aa54045
ACR-d3e79f764d1a46a1908b582111e7cf55
ACR-e65bb91dcc5547c88c382140e4340a38
ACR-41d99b0075574dbfa8e3ef3fbfe5ef10
ACR-d1a4b2fe1c8f45909de55a3ca6d9110f
ACR-c973612e19904a0b976a171a76cd0257
ACR-d2de60e71c8d40d79f11307f42db31ba
ACR-5a2be657b36e44538d2d3a5736bfe9b1
ACR-4ad506cadbd644cc82aa7dc564081b16
ACR-aaafa47462b04278a13bf34678697784
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
