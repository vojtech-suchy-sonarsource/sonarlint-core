/*
ACR-5936d12c46794bc68bea8793fe9115de
ACR-2aba6b9e2f4f425d8afbc0fc5590ff38
ACR-ed301d2e36994445b379f467641e3cbf
ACR-382b8682c3064e0b8e2ff6dc4d705844
ACR-0dca7a88d7da418c91afb37a16faaeec
ACR-7eb5f2259de6414fa7993a98bb4c4892
ACR-923f37c11eb84a9bbb4fe5d8355bce3e
ACR-b3b9bfccb9c2411ab152527999ee25b5
ACR-8732842aa6b64a73a150d6219352c1a3
ACR-296284cd2c0b4e16b4b0efbb210497da
ACR-dbc4e1cfc8984f7cb57533235072ef02
ACR-be3dffe9a3ee4d2e84940d3050d79b04
ACR-4d994ed135254a9fa3d1f64cf02771d1
ACR-b87b925dd2c9465e86dca3784953234f
ACR-f6ebe7146bde4713b3706bf25b4fd6b6
ACR-1e246cd254b44e2596067708bfc1c741
ACR-bdd1e7ff40cd4909bc1b47d720c57f26
 */
package org.sonarsource.sonarlint.core.repository.connection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.commons.lang3.Strings;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;

public abstract class AbstractConnectionConfiguration {

  /*ACR-08f25f322cad4341a56725ce652d0f58
ACR-a9d4e724288a4af4821c45de664023a1
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
