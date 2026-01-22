/*
ACR-fc63a268cfa44d3c91bf2e6e78cff324
ACR-7fddcda4720340d8b322809ce7ccbba4
ACR-e5118cbf6ee446f399643256bedb3ded
ACR-41456ff98b814a6984445790c1fa8c86
ACR-e866b2cce82d4920b619a0e07fb88187
ACR-6ec4709ba8c94a079acabd8ad0172918
ACR-c2a0d36b49d64207aa438edb4f5e4fa0
ACR-cb9be61ac2494974a21859414d906974
ACR-ed549ed792244f58b038ac7c612509b0
ACR-9949d52a2bee4f69970ff2fe7a27e400
ACR-9ea4cc04dc694979a267ab6c7d7139ed
ACR-2648fa4b4d1a437ba457d0e4519ba12b
ACR-df22b582d6d7417c80ef7e10952868e9
ACR-a4c304ee1b2c48edb6ad9f3a442c687f
ACR-1d0057c6d4044833a54fd1a548036036
ACR-e2ef49040c7848c499444a107103d67d
ACR-6ef5fa980b14433394515a2107414683
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
