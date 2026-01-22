/*
ACR-b7132e0e40704c358c6827dc019eacc4
ACR-8bab3ced00f9420da682b2492d1fe0d1
ACR-ea84287fcd4d4a498700fd7f8d77718d
ACR-095631df8a264b73ac07b9fb42f9c55f
ACR-7611c981be3746d993679ae4591ddc4c
ACR-439f64c315644783bbceab94c0b4d196
ACR-f02553f2e57247118e1efaa651e62483
ACR-78a7e43e1c614fe192b3472d836b8bd5
ACR-40363fce8a2f4399acbe087ebba4215d
ACR-76b51e0e7a544f5fb64b2299f235c569
ACR-e65dcfba180f4b06be10e8bcbacec29f
ACR-ae0bfaf0ff724f049f427357125516c5
ACR-5221640c22ce498b806757f59c64f853
ACR-bd3916d6f7fe4babb8fbfd815142bd32
ACR-240fc7a1b6a546d68788c13a9ac5f465
ACR-2f44f77979fe4213a48b780059195587
ACR-679f3459474743b78dc405842b8a4427
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
