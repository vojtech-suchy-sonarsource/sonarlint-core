/*
ACR-4965ae9638184bf5bae27fc60241dd33
ACR-d3496f1374f845788512f53b5d2bc8d9
ACR-5f419b5e33ed40b0a119365dd9975fbc
ACR-9171a908660f45f7af605334487c382f
ACR-b98dec05bae74549bafbc9df668271c2
ACR-b9c1ab1a22394bc9b3a8f7001e7e40c5
ACR-13f64b0f78804a5aa49677600c3b6d33
ACR-fb5e0e5d1d55432eaf1feff3351d1de9
ACR-63820bf66f974cbab12a8ae1fba56a73
ACR-47486b29dcba4f8eb1218713c55111d5
ACR-1e3b3faa73654650bf6f19893f9e1fa7
ACR-970108fb69f84f56ace5cf7e3ea16070
ACR-282716532d8a4520a9c80c1e585f1002
ACR-bd097fa630c94ad68ebef543fe9f83af
ACR-632490b1dc054a688bae0f6b86e984a1
ACR-4f9350038ef14f02bffa3791a79a63f3
ACR-e80b7517cb5140b9bae7b5f7512334be
 */
package org.sonarsource.sonarlint.core.local.only;

import java.io.ByteArrayInputStream;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.IssueStatus;

public class IssueStatusBinding extends ComparableBinding {

  @Override
  public Comparable readObject(@NotNull ByteArrayInputStream stream) {
    return IssueStatus.values()[BindingUtils.readInt(stream)];
  }

  @Override
  public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
    final var cPair = (IssueStatus) object;
    output.writeUnsignedInt(cPair.ordinal() ^ 0x80_000_000);
  }

}
