/*
ACR-da57442a04f64186a75e193e81bd500d
ACR-fbf049d5ba894cd7ad641707cc06cff5
ACR-fe06bb28992a4a25b83f063e4db55f38
ACR-913d5ea47530428e9a57ebc673ecee0c
ACR-a9a918a79c35448f93dc0f6d5dd73464
ACR-ff29fb39a74f40b2b62d3ac48da1fd85
ACR-a174dc4696ff4ca499b19d7dfb2cb7ae
ACR-28d8d6c8469b4f0d974c83e320828f56
ACR-e349222d47494c00913af4a3f2ea33bf
ACR-30ce095e00be4ad8b0a7bc4eccd3f850
ACR-091daef262c645f193989141262ef1a7
ACR-c7de40c62b6346a6b82aefae98bd9009
ACR-a3ceef721ab64fff95e5b6bdf2bb02d6
ACR-112bfc80a077496881438da0c416d49d
ACR-8040f40c0f614afdb6d148497e209d21
ACR-4a0ace0cb28c46babae7c958faabe23a
ACR-dbfcba1ac92a4d4a90b5d86c1156b251
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

  /*ACR-ad41802aff354ba7abff0ea2ddbd3690
ACR-ebfadbffef094aa8863c9afd51c81286
ACR-c15dc1eac523424a89e4a41751d1f792
   */
  @CheckForNull
  public AbstractConnectionConfiguration addOrReplace(AbstractConnectionConfiguration connectionConfiguration) {
    return connectionsById.put(connectionConfiguration.getConnectionId(), connectionConfiguration);
  }

  /*ACR-535420d1387341e985ece72b30dc55bd
ACR-3fb0830b31ad4b5fb98e285d41f0f6ec
ACR-99fdb7ccb9464247b6a87249e599d309
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
    //ACR-2d4350e349f14f44bcdd2fb2434039ee
    //ACR-ffc561b7b301499186451d4a2f39523a
    //ACR-91d5d58f33b343de97fd8467cf063587
    //ACR-40d4160d296144f3aec3b2c214ef7823
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
