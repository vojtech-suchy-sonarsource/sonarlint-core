/*
ACR-1ff0560513c24c9b99e34200163efec5
ACR-8f011c960b97494bb84a7e72701eb802
ACR-54dd9396d293445aae97faa0be2037b5
ACR-3295e11de28c4c5a8857cbac10611360
ACR-0d61dadfa6e044469882d990be8f7214
ACR-0e3ec0076444416ebd96704d7db17868
ACR-6d2a0628c4524be09763c98f9540800f
ACR-4dcd3e39243646a799b5cea607f96d19
ACR-908dc458541245ff852326c86c56b2be
ACR-8bed742a93fc4fba9abdd51df1a7ca32
ACR-a1fc0cd8693a4df5a071fb738665555d
ACR-2b2a67997ae04dd5acffc842f1fb7257
ACR-dd6758e59819472d8f15c7f6c6f752e3
ACR-66777282ea8f4462946eeec9410f375b
ACR-407ade4a72f44949bb29f1009cec34fa
ACR-eba6cb3227f243149d3c044e40b937dc
ACR-dcb28417b8f34e158f1eb307838f269a
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
