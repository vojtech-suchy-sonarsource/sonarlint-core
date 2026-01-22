/*
ACR-753be09a060b4ccc8040ffab8d36a8f2
ACR-7401db876eca4274af855e1059abed0a
ACR-fb943c92437d40fc8642f219315517ce
ACR-bb246f3e6236458092e405761b445e6e
ACR-252d9d8dcaa04da5a5a5c12ca3e8e59e
ACR-28964baba04649a68b6467a3920bdf1f
ACR-0f5290a667e54d84b1cef1b1d9cf9eec
ACR-2ebf47ef930b4e3d8e74099e6e440823
ACR-53aba692de6047cfafebf96057ecd17a
ACR-57aee5229ae34733be3075ab6c1dd68c
ACR-67dce87809304f7c941fe83b309b2476
ACR-f6e992bbc7924a82a24591bc2b17e699
ACR-5e8b40dc7fd740eaa3dc13e5be050eba
ACR-9ddc4712e9db413b88c3e5af0008526b
ACR-d36703ffa5b44c368feaabbb8e579889
ACR-d2aad03c927b4ea08cd41967b0acc66b
ACR-f58dbd50e49b4f02aded3c259e3671d6
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.ByteArrayInputStream;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.RuleType;

public class IssueTypeBinding extends ComparableBinding {

  @Override
  public Comparable readObject(@NotNull ByteArrayInputStream stream) {
    return RuleType.values()[BindingUtils.readInt(stream)];
  }

  @Override
  public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
    final RuleType cPair = (RuleType) object;
    output.writeUnsignedInt(cPair.ordinal() ^ 0x80_000_000);
  }

}
