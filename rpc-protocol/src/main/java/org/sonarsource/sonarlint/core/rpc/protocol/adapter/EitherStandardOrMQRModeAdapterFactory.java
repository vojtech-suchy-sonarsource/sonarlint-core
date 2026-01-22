/*
ACR-e376a4dec16248e9896ffcbaa1382b62
ACR-57fc51c85d7b4b02b9d67a65a6831f30
ACR-16f4f8bc7eee4732b1c015198b8f8fd7
ACR-908657786e7e446482f65277006db336
ACR-a2c06c158ce84e568b577a61d8143162
ACR-50c5a9b22d544802a186454fd3cd4d9e
ACR-d15fa9830dca4a38ae75b78e1e2a7564
ACR-e1c62048e5684ec49d91f6d53a817a7d
ACR-386f8cd08d8344e4973831cad7759338
ACR-5b5facc94543437f89333830af6dd8e0
ACR-42167a6e06c74fab9259f7aa37722d0c
ACR-2220b9ccd17744cfbac3ec7c116960f5
ACR-cadfd0edaca54f3ca71c40ec64d48842
ACR-9bd61abfeffd4436a2d30673b2740d58
ACR-9db4326ff38542cb971823a242b4d43f
ACR-6564f5972325419db7279081c8ad7d53
ACR-300fe805cf7443cbadb8aeeaccfd4f50
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.reflect.TypeToken;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.MQRModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.StandardModeDetails;

public class EitherStandardOrMQRModeAdapterFactory extends CustomEitherAdapterFactory<StandardModeDetails, MQRModeDetails> {

  private static final TypeToken<Either<StandardModeDetails, MQRModeDetails>> ELEMENT_TYPE = new TypeToken<>() {
  };

  public EitherStandardOrMQRModeAdapterFactory() {
    super(ELEMENT_TYPE, StandardModeDetails.class, MQRModeDetails.class, new EitherTypeAdapter.PropertyChecker("severity"));
  }

}
