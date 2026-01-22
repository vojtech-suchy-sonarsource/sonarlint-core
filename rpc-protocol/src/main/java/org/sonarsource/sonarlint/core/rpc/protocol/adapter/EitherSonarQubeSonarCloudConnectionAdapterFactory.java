/*
ACR-94ba9df18e644f46a8021ce647f5d588
ACR-64189430d33c4a77881e69c68a20cc62
ACR-5a4724b6ec114851ad4fd3346bd09b96
ACR-fec023a89b4045b0877d6e465927482b
ACR-7ca4e3028f50439e9944129773c55678
ACR-ee659dabc53548e0b173fb8fa94e553d
ACR-fedada7f8444456081703a6931db6d23
ACR-30ddeb84b7654237a6b8f25afb5d71dc
ACR-d734d79cf69f4032aa9b6b022aaf0634
ACR-d32763046f0c4449baeb4e423e30b25a
ACR-a24905b5de3f423081e9c82071ffc5e4
ACR-299fe8590b6844f9ad85bcd9d92641d2
ACR-f963023869b44e939bf0c0f8a84ad251
ACR-c9e1c046adb54ffdb818a1e199b17790
ACR-4f939ad9789f415ea92e0d898e2e8a2d
ACR-4117d8c07718405babb8094d81113859
ACR-94dd05462fa449959816cefd1fa25338
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
