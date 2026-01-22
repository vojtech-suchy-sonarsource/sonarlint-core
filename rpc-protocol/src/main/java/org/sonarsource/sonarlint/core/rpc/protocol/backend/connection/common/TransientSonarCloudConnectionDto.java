/*
ACR-1c24b6e5218c4d10a69d08bf4d4005b9
ACR-e0f32083a4a146c1be10dbd3fcf1a233
ACR-c552d5b69a41411fab342b203c3625ea
ACR-d6e796d928464c08a834689c6f30e128
ACR-83d0d2dc24464f0fbaf14b4d04725076
ACR-2f67d02ae2fa49038bf9135f0a6bf79c
ACR-0379ecf32f624ba9a519818052b45998
ACR-be29119238f74bc8bf822760a100bf49
ACR-dcf7195d64e64073a3173a08f97731e5
ACR-0151aef7932f4121958c9a42375f94f1
ACR-8f289b8d2e054adca0a6dbd769ce3cae
ACR-5d119f817c6641b3ab693fbe30f36d10
ACR-f8a98fc91ca44cb5bd4e0dfd5f25bcdb
ACR-efc28ee088de4387812a9c9300391eec
ACR-7c79511e23a541bf93d3ea9c6fa5cfe8
ACR-0017b8f8fc2446ae8d5e9221894dfb2d
ACR-e172b8b2bf7d44e182f09283aa5b3a9a
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
