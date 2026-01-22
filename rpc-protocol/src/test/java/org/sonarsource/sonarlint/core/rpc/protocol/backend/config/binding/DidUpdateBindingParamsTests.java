/*
ACR-e3ac5d5940044e47aff8062d91e457d3
ACR-003c4fb45d964fb0b2836c9cc7198eae
ACR-9db212bbe2bd4b5fa6a9139f321c480e
ACR-5f9e2f5eeb0e462cbcfdad59f99f943a
ACR-30af1d2d955745cd892b5aba1f4411f8
ACR-808949ed6a9a43a7b0bcf1a4fa07c61e
ACR-0437536da9c7424586ea9768f6997ef1
ACR-70a7555edb124a37a3e364de263e6e01
ACR-8ef6dee404134556accb1f2376d15424
ACR-8f6e2df3ee0c41c8a055aeb111e1e847
ACR-575a0caeede346bfb2dd4d9284133d8c
ACR-eddd08cad95e4f6cb1ded5921d0fc3da
ACR-63e47bc8e77448bd84d0ba16c4d12706
ACR-015630ef1a8d4cbf88476626f560f87a
ACR-45d91abfbe2448429744ce41c491ec30
ACR-7a535b325667426880523b563458c2b1
ACR-46db641e069448509099c62bb5478d6d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DidUpdateBindingParamsTests {

  @Test
  void constructor_keeps_mode_and_origin_null() {
    var dto = new BindingConfigurationDto("conn", "proj", false);

    var params = new DidUpdateBindingParams("scope", dto);

    assertThat(params.getConfigScopeId()).isEqualTo("scope");
    assertThat(params.getUpdatedBinding()).isEqualTo(dto);
    assertThat(params.getBindingMode()).isNull();
    assertThat(params.getOrigin()).isNull();
  }
}
