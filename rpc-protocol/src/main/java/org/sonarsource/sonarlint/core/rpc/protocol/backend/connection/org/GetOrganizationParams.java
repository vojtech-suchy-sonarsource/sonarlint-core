/*
ACR-aa41f1680ea347df85bc26a11db17701
ACR-40490cee2e3b43b1963fcbaad52d9629
ACR-cb282abe0fef4ea2981ac48cad5b5716
ACR-e5e4c663e42e4d479bb8ef553d631129
ACR-725d8133b80c4b759bf52489b171f34d
ACR-6d88b9f221f8407f8a33f3f8b4fe87cb
ACR-f1d9927ad3164734b71a3f39ff3c02cb
ACR-e6fbbc7e11924c4ebc3febf805c7f1e2
ACR-63cfe32da3a6401fb916c988d86bf811
ACR-e42e44774d584847b8a334cf47e30341
ACR-98a8a45aa92a4b6caa4afecc6e88f62b
ACR-7e9502b8e544453c9b8a3511c041fbd5
ACR-96fe5f9b6a014e689adb1d478c4a5cb2
ACR-d9992bb346ac4404bd5793c04e302134
ACR-89a71ac171754980bbf0d552b1f34d57
ACR-13f1ead3896043d88c2f99720b86071b
ACR-9dbb753dfe734a85a10c8f8c1ca8eff4
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherCredentialsAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class GetOrganizationParams {

  @JsonAdapter(EitherCredentialsAdapterFactory.class)
  private final Either<TokenDto, UsernamePasswordDto> credentials;
  private final String organizationKey;
  private final SonarCloudRegion region;

  public GetOrganizationParams(Either<TokenDto, UsernamePasswordDto> credentials, String organizationKey, SonarCloudRegion region) {
    this.credentials = credentials;
    this.organizationKey = organizationKey;
    this.region = region;
  }

  public String getOrganizationKey() {
    return organizationKey;
  }

  public Either<TokenDto, UsernamePasswordDto> getCredentials() {
    return credentials;
  }

  public SonarCloudRegion getRegion() {
    return region;
  }
}
