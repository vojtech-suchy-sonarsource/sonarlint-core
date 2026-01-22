/*
ACR-1a8d4fb484304128b32b816030f5c873
ACR-57b5b9840a6349deb0a51ebfcfe02165
ACR-0cebfde8c905469a83316fdc0d52e745
ACR-1480709f10ea41a588787cb1db3f610b
ACR-9d20e233e7d94926927b792b4a4d66e8
ACR-142966e27dba46c0aac4086a343292c0
ACR-42acd48715bd4685b754b1ce88cce9d1
ACR-89550a6a7c1f444ab61d445c01039bad
ACR-53da79c42f66479b99906cf42f6e2d0a
ACR-2f96add7ec2148d0bfa2e37a5f0a699e
ACR-9de01016499e4d53a09c8cd68a0ff92b
ACR-65eba9b74b3a4e9b8bfcc6a724b17d47
ACR-7d627e71f7f04f5c9976d7315a154cde
ACR-ee711183c44e4e608ac635f656b40c79
ACR-d1e8efe9373342a28634cf38e93953d5
ACR-d04ce0ab70db454582e00bc8c97448c0
ACR-a62b32958c5946b8b8442e0d20afbd5d
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
