/*
ACR-701af23f14954901a7702dc723e052e4
ACR-8ac6074b085542579cb0037b801ab440
ACR-45bb1380e80e4ac0817eb97994a4ac24
ACR-4a687b27ab5547848de03d4eb9efce78
ACR-b3c984883e984a75b41d583113d25077
ACR-50494fae86074d459ea25af3eb7b8914
ACR-baf9ae30c12c4867873d9034d125f623
ACR-835720615b574d40b4bc40d2fd8413b0
ACR-7b8534ef31bb43b4968acae14bbdb105
ACR-8cf7e3503a394348a9322dba9ce09ba2
ACR-0fc202e35a264a4bac5905f1dcea9497
ACR-87fd49892968455e92b531f8a8312425
ACR-e6398b6c6deb4ff18a731d51515e0d1a
ACR-66d59fafea454e3fac6dfae65005deb9
ACR-51397e1dd746491fb68521fd90545d05
ACR-c1ed196969584dfdbfdaeb757d147ee8
ACR-2fa5bba7dbc74405a572e97a30b5f7c1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherSonarQubeSonarCloudConnectionAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class ConnectionSuggestionDto {

  @JsonAdapter(EitherSonarQubeSonarCloudConnectionAdapterFactory.class)
  private final Either<SonarQubeConnectionSuggestionDto, SonarCloudConnectionSuggestionDto> connectionSuggestion;
  @Deprecated(forRemoval = true)
  private final boolean isFromSharedConfiguration;

  private final BindingSuggestionOrigin origin;

  public ConnectionSuggestionDto(Either<SonarQubeConnectionSuggestionDto, SonarCloudConnectionSuggestionDto> connectionSuggestion,
    BindingSuggestionOrigin origin) {
    this.connectionSuggestion = connectionSuggestion;
    this.isFromSharedConfiguration = origin == BindingSuggestionOrigin.SHARED_CONFIGURATION;
    this.origin = origin;
  }

  public ConnectionSuggestionDto(SonarQubeConnectionSuggestionDto connection, BindingSuggestionOrigin origin) {
    this(Either.forLeft(connection), origin);
  }

  public ConnectionSuggestionDto(SonarCloudConnectionSuggestionDto connection, BindingSuggestionOrigin origin) {
    this(Either.forRight(connection), origin);
  }

  public Either<SonarQubeConnectionSuggestionDto, SonarCloudConnectionSuggestionDto> getConnectionSuggestion() {
    return connectionSuggestion;
  }

  /*ACR-a9b9ed8428cf416aa1889fd43a523f6f
ACR-84c5a3925fa4492cb0ad58e9a33357f0
ACR-6829243c0ecf4feca5b990fe29c82e7b
   */
  @Deprecated(forRemoval = true)
  public boolean isFromSharedConfiguration() {
    return isFromSharedConfiguration;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }
}
