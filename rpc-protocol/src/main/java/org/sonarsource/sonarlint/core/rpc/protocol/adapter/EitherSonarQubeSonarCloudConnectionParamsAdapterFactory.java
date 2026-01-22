/*
ACR-747058df126d414baad02dbe43f7b849
ACR-a275d961036d401f89cf7f2f05dca6b5
ACR-3ed606924269432c9a6a472bed2c1ebb
ACR-72e1331b83e34a23873b14cd57659970
ACR-aa38b8e3e99e4ae589a6b250b18afceb
ACR-e9819c85001f43059706ab1f6cd93b6f
ACR-16fc37cebf904c6b97c1d7e1b249a827
ACR-4bd4b4684af2437dbdfdda51f81559aa
ACR-aff7cb143d244c8ebc1dcb2f41dbb11b
ACR-228d698dae9d42908c9b790207c6b646
ACR-57687811fa984f8a85f7a809cee41f5f
ACR-aa166844b4c746f480429b96f10dbbdf
ACR-54feb961e2104c409d40bd99d49ac3b9
ACR-49a43f214dd64c28a9c5a3b08a559bfa
ACR-9da61f9b305c4f4c8e6a6e2224610ca4
ACR-e117916140614824bd860f6cdcadec8f
ACR-9bebe66bcc1c4ff394a71db7464f3cb0
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
