/*
ACR-e09ba71f98f440e6872b0a998df9d18f
ACR-1242552076a843cdb608f8cfe15fe54f
ACR-40eb7a59663344229c6e407a8346e463
ACR-6623184be58e49b09b6c50dd81fcb742
ACR-33581cc164f34501a8c726ecdd0ad886
ACR-205a77fda61043d89deb72110f50c320
ACR-17341754af034e44a1134eb0f05fee42
ACR-0dbbdb797af54a4b862bbf0b3958672b
ACR-ad12c54bcebc45f0a0204b575ff17c98
ACR-6171a15e5bd84730970bef333dcde5ec
ACR-b932cd9dff6c417c9fd4cfaeb3e2c039
ACR-f9a6d6527bb04deb8d1117b5b42c05e0
ACR-0654ee04ad0148f08c4c5ae86f0a2f8b
ACR-ffa23ac218d84c60a32308e96b6d2f07
ACR-e8fa0a390cd847c7900855433711d23e
ACR-42b27574a15943e186dd4efed421bed2
ACR-63755789bb7c4969a6a7a5f744ff8467
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.reflect.TypeToken;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarCloudConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarQubeConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class EitherSonarQubeSonarCloudConnectionParamsAdapterFactory extends CustomEitherAdapterFactory<SonarQubeConnectionParams, SonarCloudConnectionParams> {

  private static final TypeToken<Either<SonarQubeConnectionParams, SonarCloudConnectionParams>> ELEMENT_TYPE = new TypeToken<>() {
  };

  public EitherSonarQubeSonarCloudConnectionParamsAdapterFactory() {
    super(ELEMENT_TYPE, SonarQubeConnectionParams.class, SonarCloudConnectionParams.class, new EitherTypeAdapter.PropertyChecker("serverUrl"));
  }

}
