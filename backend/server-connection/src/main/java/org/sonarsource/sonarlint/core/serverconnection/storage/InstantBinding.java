/*
ACR-290c92292f4b42839986b28cc386bc9f
ACR-b27c9462044a4299878d1b9a50be787a
ACR-41ac0c3c78f94dd395a3f3a0f607bb0f
ACR-2516b10f37e64fccb2b472ab649174fa
ACR-a246c95a61684fe0a1798e71344a230c
ACR-7edb9220d4184ae8bf0d9a2eb01c6d0d
ACR-d12b59c0fe8945cd93493fb55deef4b5
ACR-228ffdf441c14bf4949f8a22eeacaa96
ACR-6b9c785c07174fe7bff89b8273581f23
ACR-9b38e302d7cc4754970bb45fb626265f
ACR-d96b93932b3642ada01f3e0ab0a8f4f5
ACR-06efa1be135d4cd698eb449f64c89175
ACR-bc5ab1a6cbf04a898f6b3e83ed423221
ACR-4244bd3dc7fc415c88b47a715cd7eee3
ACR-88db7c33565447dc90aaaecbfa43ce4c
ACR-ca3ab835282f482abb3a018b9a76e199
ACR-afa8b2f92a73420aac08fbd464f97f93
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;

public class InstantBinding extends ComparableBinding {

  @Override
  public Comparable readObject(@NotNull ByteArrayInputStream stream) {
    return Instant.ofEpochMilli(BindingUtils.readLong(stream));
  }

  @Override
  public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
    final var instant = (Instant) object;
    output.writeUnsignedLong(instant.toEpochMilli() ^ 0x8_000_000_000_000_000L);
  }

}
