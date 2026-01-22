/*
ACR-3a06454fae054d1f82c972cac2144b7d
ACR-64d9c21e23b546aeba4fd4f495fae516
ACR-50900b3056b44c71974b7581e71fc2eb
ACR-51dd0c3853e14a8c9ccd65ffbc289544
ACR-fe211d9d881645568475e119a0e6b5c2
ACR-65489facd0c74aa5b1ef8c9c6e448ccc
ACR-41139c0c20324527bb5fe7677c403e24
ACR-b387f51c89cc4694a24ed49c43bf2054
ACR-4809ee9f63c546d59be8a62e1b228b19
ACR-e3a5c4068d57439db29dbd605d00d1a5
ACR-790270f4ba6c497881c66a4700b64a9e
ACR-8e38a2fd5cc44db3927a16d378cd190b
ACR-ce66ba1a43284585a91b68a833d82928
ACR-3473f9118b244f519ff5b9b740725984
ACR-1f15a6d835e948d29e24bc4144953cf2
ACR-4195c478aa12426892bee204c0f5aeb6
ACR-c212725405aa407398c6c7602e512c65
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
