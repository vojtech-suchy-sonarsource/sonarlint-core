/*
ACR-a08f8e62123b403996851b59c821684f
ACR-ed2f97aeba344243873c6e1626344ac5
ACR-162b2e71376f43bd81679afe3377bc7d
ACR-70459c6f963f48bdb01fa72e269d9294
ACR-4fe28b5433af4077ba3d4448fd78f838
ACR-31f30abecb4a4bc7b4d1cc24a2659242
ACR-30fb1b2b75534cd18c1713770bf14ffa
ACR-840fb3d07e964081b00c1207a94f8ca6
ACR-554626fa3ba94956a4715d025f109c5e
ACR-ac336c2c4ab94834a759eddd4fa5f93b
ACR-a6a60b32f6b141548ff1ac560d632e65
ACR-30f0b7dd17b5426b85456d8e39df5c6f
ACR-c287697c76844767b217627de834970a
ACR-498fc92fc67b4b3ebee05e1b8f17654f
ACR-f42d1b30fb934cc7af03947cadb5b429
ACR-38e54e098b0e495ca2c5a3316cfe5b23
ACR-63373bc065bf46c08fd2a3cf02b64ee7
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.reflect.TypeToken;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarCloudConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarQubeConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class EitherSonarQubeSonarCloudConnectionAdapterFactory extends CustomEitherAdapterFactory<SonarQubeConnectionSuggestionDto, SonarCloudConnectionSuggestionDto> {

  private static final TypeToken<Either<SonarQubeConnectionSuggestionDto, SonarCloudConnectionSuggestionDto>> ELEMENT_TYPE = new TypeToken<>() {
  };

  public EitherSonarQubeSonarCloudConnectionAdapterFactory() {
    super(ELEMENT_TYPE, SonarQubeConnectionSuggestionDto.class, SonarCloudConnectionSuggestionDto.class, new EitherTypeAdapter.PropertyChecker("serverUrl"));
  }

}
