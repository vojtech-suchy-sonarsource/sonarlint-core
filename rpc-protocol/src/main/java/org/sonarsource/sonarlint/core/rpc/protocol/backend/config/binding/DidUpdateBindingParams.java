/*
ACR-dde64e3d15904b24a449624bcd2d8059
ACR-764246f2df764a01b2f6f569637147f8
ACR-c56ae12004b746bc856a4a005bfa7476
ACR-1ffd2f09e3454ba693916146f6d88fe4
ACR-6d6feeadbe16490dbd0683fb2ff93329
ACR-8abe15f5211b49f39de6283b33b966f5
ACR-bf7bdfa49a0e431e9f02528a64b1ee81
ACR-3341448537b248b6b443e8ddfca9341e
ACR-db7606cb748f4e4598065253abbf5df1
ACR-2cc6f697e9be4229ad264c8b935c1be7
ACR-7cbd2eb3b50f42929e04b265a2352524
ACR-a120cdedfebe451cadfc79a644e8b4a6
ACR-08d0ac6c7d60404dadbbff378688d907
ACR-839b1cfd4bbd4fa580f0539361d1f2aa
ACR-58e3293d09e0437a80c8c47435f5ed48
ACR-2e1d3733832e47d7ad0943712a9055b6
ACR-900d00f0daf9412b94672f7a6bcb5912
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding;

import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AcceptedBindingSuggestionParams;

public class DidUpdateBindingParams {

  private final String configScopeId;
  private final BindingConfigurationDto updatedBinding;
  /*ACR-c2e51b8667c44e3aac119c53072e110c
ACR-38d561b19b9f4bbc9a4e3978e316b7b0
   */
  @Deprecated(since = "10.37", forRemoval = true)
  @Nullable
  private final BindingMode bindingMode;
  /*ACR-57d8ca9a91f04e1aab424e60af244a0d
ACR-7049339beeef43ee98df3392796b4d12
   */
  @Deprecated(since = "10.37", forRemoval = true)
  @Nullable
  private final BindingSuggestionOrigin origin;


  /*ACR-d291eaf38b334de5b9962fcb3c3be0d6
ACR-a3237b62ac6247c98ac0e57ce66a9196
ACR-fae65d2f666845139c1d0769273ee7bc
ACR-72d5678d2044478390407327ca9cd2a5
ACR-0383979680e544fbb71cded106d9b31e
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
