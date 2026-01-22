/*
ACR-557b8989adf040e58a671529deb785d9
ACR-b4da8d118d0c4b1797f1e3ee8cf5e638
ACR-b9ab5eda5e8840a3ac4b68662227834f
ACR-1a3428ac460144fca7641d11467900cb
ACR-6b33f90caf4f4e18b5a3c81905cce90b
ACR-5aaaa2c98e904b72bd9719800b96551d
ACR-9c037b1a93814e409606bfa346e567e2
ACR-2de015315a214f9193b625ab2f79c810
ACR-3a974a00922044c1bd935b87dcd74f6c
ACR-1a0225a413d54bc4b7ce2d82cf575a9c
ACR-3e4c50c22545419ab18f07bc8594f396
ACR-c58ed44a42f14ca5ba9406ca9947bf40
ACR-58ec8af499dc49a49cbae7faef4a0f66
ACR-d525d460e1fd4ae9bfd4b6232898446e
ACR-16488c35cdc148209af34fe069619b10
ACR-1a3b6c8c7a564d1b94deb57bf2d3923b
ACR-09dc848ed2844f1586c5f91589555352
 */
package com.sonarsource.plugins.license.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicensedPluginRegistrationTests {

  @Test
  void testBuilder() {
    var underTest = LicensedPluginRegistration.forPlugin("xoo");
    assertThat(underTest.getPluginKey()).isEqualTo("xoo");
  }

}
