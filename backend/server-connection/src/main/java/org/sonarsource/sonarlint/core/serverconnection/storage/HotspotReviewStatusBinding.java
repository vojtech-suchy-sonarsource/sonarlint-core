/*
ACR-2521eb0d6ee842569b5ec8681a3d6e84
ACR-91a9205b101a46b9bba972624d8f2010
ACR-0208abacfd3e42c497cc052aebe42152
ACR-bad34d06732449bfa235d4ef681a7c2c
ACR-b9b8448e35ed413090b6c949d42ea8d7
ACR-2d3800dd1daa4f8599869c1d48307c46
ACR-f6150257d0eb4166aa17a8df1aef9ef2
ACR-65bf015725184fecac58ee3627a7e944
ACR-8aff8e851a0445fb90664ba8c160ba6f
ACR-34433d4a59e948e4a585e5fb8fc29217
ACR-7a296caeb5be4f66a8f79fb837b45650
ACR-1c4e48c2871d4436928ab66a8e182e6f
ACR-6483cb620a294c808cdac9ffa1b4cbef
ACR-b17500e6b2974d0082a785852d7989f0
ACR-54b7db31c9404b0185d6c77382efb1c2
ACR-49ee7707e99d48d38af1141e3db6ee3e
ACR-3818abba962a454d807878851fe7cba7
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
