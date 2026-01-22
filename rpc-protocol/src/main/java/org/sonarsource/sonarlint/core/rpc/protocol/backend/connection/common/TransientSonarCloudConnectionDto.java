/*
ACR-0247c0f58bc848ee8096e9f7cf4d4ef6
ACR-13211ab874304df899e5c47ab36cf82f
ACR-3f08dc9e6b8a4574847b501dd0631635
ACR-e95bfc505775482797511dd784a16b14
ACR-c882633025874ce5b3b9e9f14b237422
ACR-1c8b2840653d439d8df7db72821b741a
ACR-30a13bfdb0554111a23a41e22902940b
ACR-170175ad9187441098e7d32f5e853fa4
ACR-45bbd647cdbb49d5afd4b9298b422271
ACR-dbee42d3c1394dd7b57564fc11b911fe
ACR-db0eecf8995e4a80a66edf1cfe75adef
ACR-dacc2a27e0364ae286d3559b4de910d6
ACR-5886054b792643eea24045f1d4677b78
ACR-b96d382f87b84868810b768f2555fc93
ACR-41657316f47249399315a94d91ee0776
ACR-f554924c28364e9fa3b396f2fced28ec
ACR-55be1c9746c442c6b4e3530739929311
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common;

import com.google.gson.annotations.JsonAdapter;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherCredentialsAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class TransientSonarCloudConnectionDto {

  private final String organization;

  @JsonAdapter(EitherCredentialsAdapterFactory.class)
  private final Either<TokenDto, UsernamePasswordDto> credentials;
  private final SonarCloudRegion region;

  public TransientSonarCloudConnectionDto(@Nullable String organization, Either<TokenDto, UsernamePasswordDto> credentials, SonarCloudRegion region) {
    this.organization = organization;
    this.credentials = credentials;
    this.region = region;
  }

  @CheckForNull
  public String getOrganization() {
    return organization;
  }

  public Either<TokenDto, UsernamePasswordDto> getCredentials() {
    return credentials;
  }

  public SonarCloudRegion getRegion() {
    return region;
  }
}
