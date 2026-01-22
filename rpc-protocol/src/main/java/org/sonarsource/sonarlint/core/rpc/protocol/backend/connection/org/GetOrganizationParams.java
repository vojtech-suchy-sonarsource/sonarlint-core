/*
ACR-fd5e77f32dc044e39584e8f75e9519a0
ACR-5e0989cc3079486c8980df568b3cee56
ACR-4269d9126c83425cb61ecbc50b43b580
ACR-3b1178f3b3554e4e99d3398123a510a2
ACR-846276650c9b460aa783041395a20ee8
ACR-d83a345548de4407b033d01d9abf3e2a
ACR-1f4d4637a081472eb8fdd4ce2a949e2b
ACR-2f1e228eaace417aa362dfb7e7c7752e
ACR-402e7f1a454349a2a3218fd9726621a7
ACR-a9848984538a402ca50d8ad1ddf810aa
ACR-8873b37fc7b94efea65f30592d4147cb
ACR-0b75a5812f1547afba32909a6c246016
ACR-0824908527bf442998332140354163a9
ACR-eb784c1adf1d443b8fd04fb0140ee9a1
ACR-c6ab21997c0e42c9a9c9d098f7aa43eb
ACR-615215a44644451dba5d157a47918842
ACR-b6fc6d7d7e364b4e98d800b5944d49f9
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
