/*
ACR-650dfbfa69fc41d2b24ecc9ab7619b66
ACR-303fc713d85640c983ae3dadc5565e3b
ACR-4ee771722bc343ccbd2201fc8aefa7ef
ACR-6f9b17d072374904a2d9534868525f4b
ACR-077e6b09c9af41e7b704e60796766772
ACR-03309451871341a4ac280358900059fc
ACR-6f90d5314b994eb0914bb0b16b938bec
ACR-970baedd7f3e4cca9f14592473c8000f
ACR-3f0bca103d7d4f949583bf041ccb9621
ACR-b9342dd8ccdd459592f68a2fab27c37d
ACR-a4eee4b66fa34a78b3033abbd76e24f8
ACR-0174337d78374040a140f5d57edc623f
ACR-4a7f8c3764d84aefb71021a29cf784b2
ACR-c1d9961d25d140bea7143275e9d433af
ACR-4a6088114fa94a4eac8a234b4e500dbb
ACR-b2029622792d4e13a85be56252c777e8
ACR-ef93ae21e46b45a49a01f11ea1b73849
 */
package org.sonarsource.sonarlint.core.repository.connection;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;

public class ConnectionConfigurationRepository {

  private final Map<String, AbstractConnectionConfiguration> connectionsById = new ConcurrentHashMap<>();

  /*ACR-3d53b677098f4965969ac208de5a7aff
ACR-0a19aca75f46478cbd1e0ba2deb4bcb8
ACR-a8c117ce865d40a89039c6ff81480fde
   */
  @CheckForNull
  public AbstractConnectionConfiguration addOrReplace(AbstractConnectionConfiguration connectionConfiguration) {
    return connectionsById.put(connectionConfiguration.getConnectionId(), connectionConfiguration);
  }

  /*ACR-4234954d747c4cc2b247843c66336157
ACR-a5afe2d4f7c343b5b833dd50cc5c9e03
ACR-fe0db131a9bf41dc912158f530929613
   */
  @CheckForNull
  public AbstractConnectionConfiguration remove(String idToRemove) {
    return connectionsById.remove(idToRemove);
  }

  public Map<String, AbstractConnectionConfiguration> getConnectionsById() {
    return Map.copyOf(connectionsById);
  }

  @CheckForNull
  public AbstractConnectionConfiguration getConnectionById(String id) {
    return connectionsById.get(id);
  }

  public Optional<EndpointParams> getEndpointParams(String connectionId) {
    return Optional.ofNullable(getConnectionById(connectionId)).map(AbstractConnectionConfiguration::getEndpointParams);
  }

  public boolean hasConnectionWithOrigin(String serverOrigin) {
    //ACR-7157029428634ec9b89ed7910eac39f0
    //ACR-a4a22ad9c40e448386411a0570f02094
    //ACR-c1c9a50835d74156836db7c59c35d325
    //ACR-899c7f252a494f4399e616f18c33d7df
    return connectionsById.values().stream()
      .anyMatch(connection -> haveSameOrigin(connection.getUrl(), serverOrigin));
  }

  public static boolean haveSameOrigin(String knownServerUrl, String incomingOrigin) {
    return ensureTrailingSlash(knownServerUrl).startsWith(ensureTrailingSlash(incomingOrigin));
  }

  private static String ensureTrailingSlash(String s) {
    return !s.endsWith("/") ? (s + "/") : s;
  }

  public List<AbstractConnectionConfiguration> findByUrl(String serverUrl) {
    return connectionsById.values().stream()
      .filter(connection -> connection.isSameServerUrl(serverUrl))
      .toList();
  }

  public List<AbstractConnectionConfiguration> findByOrganization(String organization) {
    return connectionsById.values().stream()
      .filter(connection -> connection.getKind() == ConnectionKind.SONARCLOUD)
      .filter(scConnection -> ((SonarCloudConnectionConfiguration) scConnection).getOrganization().equals(organization))
      .toList();
  }
}
