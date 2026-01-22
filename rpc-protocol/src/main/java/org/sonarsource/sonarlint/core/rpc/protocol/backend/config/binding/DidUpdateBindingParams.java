/*
ACR-4a44736b27e94452890a28a9602cb585
ACR-b2a4597e904541a68b7cdf6de0aa632e
ACR-0d1d901e29294564bd9f1719fe534aa5
ACR-4d20e4e35aec41f0bc9c59dd7f43ede5
ACR-fcd7d3ddbe4b4443bc94264f30211d64
ACR-aa9054412ef643f89de21c4f7e770fc1
ACR-29d1881459da46e4a7de0a1c7e386094
ACR-4137d154d0654c79a3720e1aae6a1e58
ACR-a1d7f198e83d460d942c2f1ec5bd0444
ACR-a862af3a1eb140f18868d87e5b5cf604
ACR-465746610d33476e8bd367b756897ba9
ACR-035c81c69d7f44b68cc3ac1db476044c
ACR-7d8a6a36bd184ef89eb46f168d8f5c34
ACR-7d065a2be3634af8ac3ffe9a21b01362
ACR-96a3dc1a4e164310a55b4f5194ef2c28
ACR-8950f5ba1e34419f8eb11f7a2d5b61f4
ACR-0a7dd25d876d4ce99aff693c3a8567c9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding;

import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AcceptedBindingSuggestionParams;

public class DidUpdateBindingParams {

  private final String configScopeId;
  private final BindingConfigurationDto updatedBinding;
  /*ACR-6f8e0201df7b470c9fe3f8b0b7c05a70
ACR-9330835545b5446cbb19f9c50ee402fd
   */
  @Deprecated(since = "10.37", forRemoval = true)
  @Nullable
  private final BindingMode bindingMode;
  /*ACR-45423bfd5f424149826e22b82d4f8f82
ACR-d395ccd3414a44d19b5b28803a4d95ee
   */
  @Deprecated(since = "10.37", forRemoval = true)
  @Nullable
  private final BindingSuggestionOrigin origin;


  /*ACR-1f30e4e6277943ec9a8c97c06ae0c7cf
ACR-4cb13207ce40489ebaae4221f99fa0a5
ACR-756be2a61cf44481843ba76b09b763bc
ACR-f016f7c94baa4303bb226d20d32f5679
ACR-0ab970bd948d43edaec37b2f5f3a97e6
   */
  @Deprecated(since = "10.37", forRemoval = true)
  public DidUpdateBindingParams(String configScopeId, BindingConfigurationDto updatedBinding, BindingMode bindingMode, @Nullable BindingSuggestionOrigin origin) {
    this.configScopeId = configScopeId;
    this.updatedBinding = updatedBinding;
    this.bindingMode = bindingMode;
    this.origin = origin;
  }

  public DidUpdateBindingParams(String configScopeId, BindingConfigurationDto updatedBinding) {
    this.configScopeId = configScopeId;
    this.updatedBinding = updatedBinding;
    this.bindingMode = null;
    this.origin = null;
  }


  public BindingMode getBindingMode() {
    return bindingMode;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public BindingConfigurationDto getUpdatedBinding() {
    return updatedBinding;
  }
}
