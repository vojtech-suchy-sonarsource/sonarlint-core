/*
ACR-a2ff51e277e04c6abff9153f36646370
ACR-7f5bd4bd4eb24dc1b9e9dae68acd996f
ACR-87181a4917ec4d77abd902d182a8a902
ACR-43fdff9de2594a72a7ed32dd12b1d2ad
ACR-09099690ff3b485a99f5816a8e45820a
ACR-3ba18bec96de4ab7a4e4b35318ed49b9
ACR-62fec29a6b9e47d3be0421a38284de03
ACR-5f522c8540df40a79b5c6db0e56d060e
ACR-d8507e57193e42729a61cb720c1254d3
ACR-65a1a4a724e14bc5a5fecd4faeb1ca48
ACR-5c1130f0682f41abba50edaa38e9be37
ACR-04026d7a848441d285f72930e3e882a5
ACR-19ee6c4b74fc472a87944413021763a7
ACR-476c5f12af7e45a79d250031fa4faf41
ACR-0c0d940b34c646f2ac7ddff1e14b2f24
ACR-f18f55f8148445838368b89dc71fad02
ACR-940592a8a84a4a209feb5bf5c4cd8ada
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherCredentialsAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class FuzzySearchUserOrganizationsParams {

  @JsonAdapter(EitherCredentialsAdapterFactory.class)
  private final Either<TokenDto, UsernamePasswordDto> credentials;
  private final String searchText;
  private final SonarCloudRegion region;

  @Deprecated(since = "10.14")
  public FuzzySearchUserOrganizationsParams(Either<TokenDto, UsernamePasswordDto> credentials, String searchText) {
    this(credentials, searchText, SonarCloudRegion.EU);
  }

  public FuzzySearchUserOrganizationsParams(Either<TokenDto, UsernamePasswordDto> credentials, String searchText, SonarCloudRegion region) {
    this.credentials = credentials;
    this.searchText = searchText;
    this.region = region;
  }

  public Either<TokenDto, UsernamePasswordDto> getCredentials() {
    return credentials;
  }

  public String getSearchText() {
    return searchText;
  }

  public SonarCloudRegion getRegion() {
    return region;
  }
}
