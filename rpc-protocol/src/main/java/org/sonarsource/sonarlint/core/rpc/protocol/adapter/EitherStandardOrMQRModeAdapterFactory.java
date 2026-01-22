/*
ACR-1663daec670b4ac1b777561ad2a6dd11
ACR-31945dc26ff04f28813c565a3fdd77d3
ACR-02af7e30fdd34276a0d7d7d9e6495801
ACR-8247bb814fb24abc8567cc4900ed4909
ACR-dc396bd476074485816dfbc2e9377d1c
ACR-22590b28a0e24d35a0b7b69bc67bb7b8
ACR-d32f5e367039422c8c0e5f3a145f0375
ACR-2114bcf33c964e8d951ce77b41288bcb
ACR-ff9f230083284f9ca5168d1d276052b6
ACR-487f15eb177b4cce9e545dde5e7e41c6
ACR-f0322d31b9a743718db917b533b5247a
ACR-c94633998ee246ca9b9cbdfa4dc6b9d4
ACR-ad05365d5bc64b87be0ac8f934c776a5
ACR-0b24810b13204b29b837b301719fa420
ACR-b46fe5a40b704e908072737c153e58e9
ACR-086bc8e17b2047849e87916f99fdd985
ACR-8c73373a083642fa9b19a21791dd0872
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
