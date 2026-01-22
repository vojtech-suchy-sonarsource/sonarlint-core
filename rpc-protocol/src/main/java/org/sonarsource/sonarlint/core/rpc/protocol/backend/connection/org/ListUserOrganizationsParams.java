/*
ACR-03a0d372e8334e4ea830c5010a51ab46
ACR-6f40b65995ae495f9c0c77b2d82d8741
ACR-285e10a77a564ddd9d66f392c3039706
ACR-a1a6c63d4dd14bb7a012a470033d6f93
ACR-dded795224fc4bb39fe0fabe7147b986
ACR-be3c015b3ee24fd89003febe65885f46
ACR-8a6c8345008c4dc2a12f99f10be6cdc3
ACR-06a0a155a02245f3b7bbdfcee490b6e8
ACR-bb928ce69e45438aadff93a5072af527
ACR-3c35b09d420f41769857ab3d59584990
ACR-82f16109d1e140bc83dff7ab8156bedb
ACR-defc9b6f5971422ca6e2c4261d92e616
ACR-3eb837679ddb448e8d63a64983a98102
ACR-4b86947661534a7fb654c83c8332159b
ACR-1c2941c5c28c4dc6af3885cbddfd9e3d
ACR-d7a5af34f667492886831833ad310671
ACR-fefe6ba827004731941876016264fba5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherCredentialsAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class ListUserOrganizationsParams {

  @JsonAdapter(EitherCredentialsAdapterFactory.class)
  private final Either<TokenDto, UsernamePasswordDto> credentials;
  private final SonarCloudRegion region;

  @Deprecated(since = "10.14")
  public ListUserOrganizationsParams(Either<TokenDto, UsernamePasswordDto> credentials) {
    this(credentials, SonarCloudRegion.EU);
  }

  public ListUserOrganizationsParams(Either<TokenDto, UsernamePasswordDto> credentials, SonarCloudRegion region) {
    this.credentials = credentials;
    this.region = region;
  }

  public Either<TokenDto, UsernamePasswordDto> getCredentials() {
    return credentials;
  }

  public SonarCloudRegion getRegion() {
    return region;
  }
}
