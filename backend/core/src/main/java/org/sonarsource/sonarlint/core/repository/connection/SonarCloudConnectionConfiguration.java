/*
ACR-1b7ccf463dcf4adcbad6dfd01c7f3ab9
ACR-4f9b2950e68b4dfcab62c9b13c96f9a1
ACR-fd1b2ce1193f469284a8d39d38c2532e
ACR-7512920905ad43d1b260aedc7409ad4f
ACR-7db804de545740d6a7927f6de9911a3d
ACR-d977277a78e241ce83567b8d9def5311
ACR-2b05265cd6de4e09a0a9995bb173e8f1
ACR-1c656f2bc84d467da1badbd7414762ec
ACR-0c92631fc4204ab3b6c7bbaae18f9637
ACR-c008af09d2b34d5b8b872b82a13864ea
ACR-6627b24f7428491f95810180557ae248
ACR-4ef4a9d57da647b895527bcb352384e0
ACR-35d9ca347d7141489a4f18af3a30c781
ACR-94704f10591a432c95d49055b2805572
ACR-aa9a017abdcc469696bbc2a3981dd1b8
ACR-10b959919e204162b6e1eba8895daa2d
ACR-96ecb091847d4852a406918235c5f566
 */
package org.sonarsource.sonarlint.core.repository.connection;

import java.net.URI;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Strings;
import org.sonarsource.sonarlint.core.SonarCloudRegion;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;

public class SonarCloudConnectionConfiguration extends AbstractConnectionConfiguration {

  private final URI apiUri;
  private final String organization;
  private final SonarCloudRegion region;

  public SonarCloudConnectionConfiguration(URI uri, URI apiUri, String connectionId, String organization, SonarCloudRegion region, boolean disableNotifications) {
    super(connectionId, ConnectionKind.SONARCLOUD, disableNotifications, uri.toString());
    this.apiUri = apiUri;
    this.organization = organization;
    this.region = region;
  }

  public String getOrganization() {
    return organization;
  }

  @Override
  public EndpointParams getEndpointParams() {
    return new EndpointParams(getUrl(), Strings.CS.removeEnd(apiUri.toString(), "/"), true, organization);
  }

  public SonarCloudRegion getRegion() {
    return region;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    var that = (SonarCloudConnectionConfiguration) o;
    return Objects.equals(organization, that.organization);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), organization);
  }
}
