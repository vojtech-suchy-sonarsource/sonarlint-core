/*
ACR-2885ca768ec84774beddc36553002c04
ACR-d15794b5134f4eefadd72c56ee322521
ACR-1afa757af7724f10adca923b08433491
ACR-93b33dde165a4892b64932e4668afc74
ACR-9843e98fbc2745758b275428c0e3e5eb
ACR-01b81e95c1804d34bf2e45a47ddedd71
ACR-5e637b9a4d2b4a8abb5355bb988d8c8e
ACR-ca451775d0404845852e56998676d2ed
ACR-1eb90e6bccff4a7795fc2cd6e0e49704
ACR-183dcce01fb8417ca1f37e04cee97931
ACR-13965c18d40c445eac18a7b6a65588ed
ACR-b6ef29c10dc94c6a9c250369e80278c2
ACR-f2013f549b9c4f0a99c7b6a307890ae9
ACR-66f0bdfd3b654bfa9c61fc571fb0a26e
ACR-d84b505a7a5b42d0a3366085bb6ae007
ACR-de3d50420b0144e38ca86734cf039a06
ACR-6d7b7f4e7e954eab8595940cd0817afc
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
