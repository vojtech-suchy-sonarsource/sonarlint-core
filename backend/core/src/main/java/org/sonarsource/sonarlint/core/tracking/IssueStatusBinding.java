/*
ACR-10e9cd40837f4368b8542af5e9bc28ba
ACR-c4da092a9ed14a00b275af25d2572d1f
ACR-65f01ef11ac94e9ead2848c84f12cdb7
ACR-dd976057cd9043658800da86614ffcb6
ACR-211185eb1ba5485b99808b07bd85752f
ACR-1f895ebe3c12413daf8194116c1011e2
ACR-55657a838a044f9a861070c36a8e2a24
ACR-59c881575fe641e7a217e1c6cea7e342
ACR-09337284001a454a89aefec93381ae68
ACR-73f4ebc844c9419d9ada9f54b6c8fec4
ACR-49fcc7986c874332a0716d9fcc30c69e
ACR-298c9f6b26194c99aaf7bdfa151797df
ACR-61af78f18acb4d5398cee36036b3c651
ACR-f40c9a1f98c14784b8b2ed2cd66e90c2
ACR-e683b7744eba435fa116ed49eeb888cb
ACR-84a7a5aff32a47269ba977aa79f03b65
ACR-aa670329350f4c30aaa0f2e448d039e2
 */
package org.sonarsource.sonarlint.core.tracking;

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
