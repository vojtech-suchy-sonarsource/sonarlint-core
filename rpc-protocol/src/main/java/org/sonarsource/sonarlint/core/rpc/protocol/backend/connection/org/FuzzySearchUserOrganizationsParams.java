/*
ACR-58708cc648ca48c7b190da43b82d4022
ACR-802f5375921e4f4ba6eb4326de3022e6
ACR-935e2ed113d244b794011547132836f6
ACR-1609f9728347455395a607d493668556
ACR-0897551a793c4444b49f0f650d733e67
ACR-96044a55188c419dba83cc64a52556fe
ACR-f2e17128f092490fb385bd4f61c8525b
ACR-1e6184a16e8d4aceb7fc651d9e196f99
ACR-fa80f218d1334d59b2727a5711eb3a1e
ACR-00cb6a1979104320b0ec6395ad5eea8f
ACR-d45a4a494a9346999d946c6d64b234d7
ACR-c89274be74d74ed5b60c82327797d767
ACR-d46409b44c9f469f9d1a23cd9fbab11c
ACR-ec1a98cf28a44884ac4966906491448c
ACR-0283bf2bee9e423fba5f1777162ddd11
ACR-522b88b10a364565a50960cf3fbea5be
ACR-7b6f93922988453682ad08dfbf1d4a7c
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
