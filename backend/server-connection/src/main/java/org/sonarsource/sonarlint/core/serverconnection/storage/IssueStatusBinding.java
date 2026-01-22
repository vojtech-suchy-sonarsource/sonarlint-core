/*
ACR-7c8e614ed76440daa969be18f0b564e1
ACR-4fc92eee10334cac9fc7bbcc3c8a940a
ACR-17499836f9eb410dae1e7b20deb12616
ACR-d155485ae15045a0bc27f5c01d7fb92f
ACR-dfd9c517335248b79b91085620914fe0
ACR-3f94f5c5224549e48ca754126c5448b8
ACR-b1b164fdc9f5416892c0ec903d14128d
ACR-7ffd978a6968416aa57a610c9a4c4a51
ACR-7940ec14e0db430da68f57f1b86e8f0f
ACR-10c84325bee04a75aa0796af9397940c
ACR-b0c39c0dbc0742149efd224a18c3be8d
ACR-ee7f424dacee4629961a27298bd49df6
ACR-e183bd076aad49e0b9588f2cd09d9216
ACR-e464e9260d294e6c8a1b08d7f628f9a9
ACR-bf2b46294aea4a698e01b576a2aa70d0
ACR-50154d03da2d436bbd0bbe6e9508ad77
ACR-c74ce5507cce4a80af76f50a8affe286
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.ByteArrayInputStream;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.sonarsource.sonarlint.core.commons.IssueStatus;

public class IssueStatusBinding extends ComparableBinding {

  @Override
  public IssueStatus readObject(ByteArrayInputStream stream) {
    return IssueStatus.values()[BindingUtils.readInt(stream)];
  }

  @Override
  public void writeObject(LightOutputStream output, Comparable object) {
    final IssueStatus cPair = (IssueStatus) object;
    output.writeUnsignedInt(cPair.ordinal() ^ 0x80_000_000);
  }
}
