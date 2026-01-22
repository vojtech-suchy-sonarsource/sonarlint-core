/*
ACR-ad41422788bb4417b3eb5b089eca8ffb
ACR-6637a6a87fd64f7f8143218d4347aa48
ACR-ef7b56dfc951414db33fe2b9799f490a
ACR-e87a70f52daf479b9287de80fb16eefa
ACR-4a4b2f08146a4b93b53749a0b5289c78
ACR-912fc3d94b37414c8bcde1760f564e93
ACR-f1581b3a0d1e4a81ae04ff4adfb402ba
ACR-c610d9772f3e41509f6855cb2a382f7c
ACR-d1430ef5fd6747e98e29b562ac027ca3
ACR-661c5e8ad19a43c9abecd39b324401eb
ACR-0c3b9dac30be4f2899f8691c79280b57
ACR-8703d854ca0845b884ae998466fdecdf
ACR-73f00c8e490b40d1918f29908aa02b4f
ACR-208df6bb1a0949a08ff714e4ba49b015
ACR-b032074f344644c78638a3a51d014ad0
ACR-b682ed3728464c85aa546a3688171df1
ACR-d2c8bec0eaf24caeb964e2ab3357a53c
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import jetbrains.exodus.bindings.BindingUtils;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;

public class UuidBinding extends ComparableBinding {

  @Override
  public Comparable readObject(@NotNull ByteArrayInputStream stream) {
    return UUID.fromString(BindingUtils.readString(stream));
  }

  @Override
  public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
    final var uuid = (UUID) object;
    output.writeString(uuid.toString());
  }

}
