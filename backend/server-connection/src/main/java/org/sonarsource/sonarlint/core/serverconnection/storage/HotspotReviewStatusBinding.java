/*
ACR-57847c1ac3b74c2b9b8cec6cef81b14d
ACR-bcf313e832b94ec38abf87a6df40d2f0
ACR-82ab2c0f4d7043e5930be176580f7d19
ACR-7767a326602a47d68302784dd5d2b9be
ACR-d34a291f96584607899c31c025910c27
ACR-37e8a497999b47c19ab2ddb7fce3995b
ACR-d3f33832dabb4554a26495627f434a18
ACR-3c0591e1a02e4cdb8c11001041f0d4e3
ACR-0c5525026d96452a9882cd30484b8e83
ACR-2fe0e23236f942b8ad0894f32d616da3
ACR-6049b7a2d0a84f88b16a857af7ce05d1
ACR-90e75406e3274dc8b06c4ce6ce274686
ACR-872eeefaf7824957b41d22bcd6b3a23e
ACR-dce48e288c3241cca87ae99bb2919479
ACR-fd37650db9db490f997bc8a134d74aaa
ACR-b9bc0358cd444905b08ede81726eff72
ACR-0cff35bae2724cc4b0aa0a013f8f4933
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.ByteArrayInputStream;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;

public class HotspotReviewStatusBinding extends ComparableBinding {

  @Override
  public Comparable readObject(@NotNull ByteArrayInputStream stream) {
    return HotspotReviewStatus.values()[BindingUtils.readInt(stream)];
  }

  @Override
  public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
    final HotspotReviewStatus cPair = (HotspotReviewStatus) object;
    output.writeUnsignedInt(cPair.ordinal() ^ 0x80_000_000);
  }

}
