/*
ACR-ae766f6be106403599befc631fff3957
ACR-16550b135ba749b6a7fa114c1f279897
ACR-000e14f6435d4c41bf007e3f1fd1e56a
ACR-171065bdd7be4bd1a3fb3c3fd60d6db9
ACR-b8c7eab2362540e09291ace27157a569
ACR-7f5e5d2e75054121a94915482f4a32ab
ACR-5114b601c7974e84a76a6decf53934ca
ACR-6fe02df0b3864bd7a26fb7c672742ec6
ACR-a6b15212709e485fb190c5a7967d354d
ACR-9a833b081110461898f8303d083d12e1
ACR-48ab245973194a1d9bc7de713c5671de
ACR-441b9425673a4e04a2e4d2af3a766aba
ACR-a2d291b671904e39bd21b0f488a1543f
ACR-2efdb45af14b4e079c434807c6c7ab91
ACR-c5df9f82a73647ab937ead81d9ca7639
ACR-b89cab0deaeb46a6aa6af0532d491f5b
ACR-0e7980688950498bab0e156faf162b3a
 */
package org.sonarsource.sonarlint.core.repository.connection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.commons.lang3.Strings;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;

public abstract class AbstractConnectionConfiguration {

  /*ACR-dab4faa422c1463eb8b7c1e791606079
ACR-ea040636b5544c158938ae3f38a0deda
   */
  private final String connectionId;
  private final boolean disableNotifications;
  private final ConnectionKind kind;
  private final String url;

  protected AbstractConnectionConfiguration(String connectionId, ConnectionKind kind, boolean disableNotifications, String url) {
    Objects.requireNonNull(connectionId, "Connection id is mandatory");
    this.connectionId = connectionId;
    this.kind = kind;
    this.disableNotifications = disableNotifications;
    this.url = Strings.CS.removeEnd(url, "/");
  }

  public String getConnectionId() {
    return connectionId;
  }

  public ConnectionKind getKind() {
    return kind;
  }

  public boolean isDisableNotifications() {
    return disableNotifications;
  }

  public String getUrl() {
    return url;
  }

  public abstract EndpointParams getEndpointParams();

  public boolean isSameServerUrl(String otherUrl) {
    URI myUri;
    URI otherUri;
    try {
      myUri = new URI(Strings.CS.removeEnd(url, "/"));
      otherUri = new URI(Strings.CS.removeEnd(otherUrl, "/"));
    } catch (URISyntaxException e) {
      return false;
    }
    return Objects.equals(myUri, otherUri);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var that = (AbstractConnectionConfiguration) o;
    return Objects.equals(connectionId, that.connectionId)
      && Objects.equals(disableNotifications, that.disableNotifications)
            && Objects.equals(url, that.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectionId, url);
  }
}
