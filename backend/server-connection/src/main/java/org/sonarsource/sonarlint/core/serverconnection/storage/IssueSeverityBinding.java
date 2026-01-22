/*
ACR-4e006f209af64bc8a4b54f87e3e4878b
ACR-200361cfb7ac4f8285885a702d4f943c
ACR-2c03566d17f24404a8ff1836d361317e
ACR-9107f453b44249d58f4aece464e70934
ACR-0422738121a34b66b997b178d7d2a9cb
ACR-0f4b589d17954a5a98b000805a234107
ACR-5b8b7b1628d44aeeaf757f16b9514785
ACR-71ce4dcecdd94d92a2cb15bcbda33fc0
ACR-c833f4f01e5e4300aa3824ef0a991298
ACR-1c5ddde447ea458b982ebbf2615677c7
ACR-3e03a55eac2a4191acc4eed87156ea8d
ACR-ceaef779afbb43868f3062949533f441
ACR-27928dee770e4657bc5829ed46977e8e
ACR-6c952037dff045d4978eb31df1b2f046
ACR-1bf04135c77244ca95207072cdebc16e
ACR-09b9f60bd72849c88813d54d736f94df
ACR-a36bb27b4cb6407f9a7243c811766c1d
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.ByteArrayInputStream;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;

public class IssueSeverityBinding extends ComparableBinding {

  @Override
  public Comparable readObject(@NotNull ByteArrayInputStream stream) {
    return IssueSeverity.values()[BindingUtils.readInt(stream)];
  }

  @Override
  public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
    final IssueSeverity cPair = (IssueSeverity) object;
    output.writeUnsignedInt(cPair.ordinal() ^ 0x80000000);
  }

}
