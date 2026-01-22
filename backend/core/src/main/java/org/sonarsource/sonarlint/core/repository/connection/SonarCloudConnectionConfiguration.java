/*
ACR-37980a94a1784a49989c978c6b7188a5
ACR-be5f48c20b8e4a8e9f88800872590e9f
ACR-ee7be9dae1d2488ca5e5bf864d93462b
ACR-56080ba796ee48be827ea2173b9130f8
ACR-c6276dad09c647a88a3c56eeed84988a
ACR-e56f71b9b4014d8db095461f14c07527
ACR-6858922f80b74c4093a3a08dfe28d44e
ACR-da72c48131d54cd5a075d2fc6bb4d37c
ACR-207129db29f44e0dba28a1c1e30a43f7
ACR-bcf16b7ce14742a8bdac698510d0a6d2
ACR-0d6df29744744041a887ff544df44253
ACR-c6312217786645eeb8698d45c1c66620
ACR-3f2cc65a0e7e44b190bbcb1ffc851de0
ACR-1acc9e388f9c458393be37fd9403f7e6
ACR-f7c7057a6f454c4db1a68f04347c479d
ACR-525cb541638140089e281250a4e3528b
ACR-da373637fd364e098be60e708d61d011
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
