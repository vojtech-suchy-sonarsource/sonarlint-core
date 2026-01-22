/*
ACR-e356c398ccd64a68835338da34b3ac23
ACR-4b22278f07a349c4915083c2ec95034f
ACR-468d533cd09344319d1ea2125a2b073b
ACR-bbbd051386fa459d809147bd3b9edf7c
ACR-ca9cdfbea75f405890415eb055fbe6d7
ACR-a49c20f4675a4645824e855df8a10a24
ACR-3d258ab86bd9430e9d0514b383eb70bb
ACR-a3f5c6bd43514a25a2801d3e91a2216a
ACR-af7e5fd41151480a9bc7f35604aa95f4
ACR-1dddf44d4f2245559cf1da52b9673ca6
ACR-5177ba37c62c4c84bfd3d39bcefc6b96
ACR-60d99248827d4565a5ee055d9c2a16ab
ACR-8b14fc4b4fcc455e807626e7b2bf1cc6
ACR-baf4ee1ab97d4d4288d896dd776bd2a1
ACR-bc07748c1be94f7fae6e062dcd3f0027
ACR-8c2bae093124421886098e7f104e7585
ACR-a07d34f1f6624c62acc1c05525b029c2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.progress;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class ProgressUpdateNotification {
  /*ACR-42b54be8e7f64a479c9d0bd365f09cad
ACR-b25a8a9df0bd47f5806c6d10a4d10256
   */
  private final String message;
  /*ACR-b513b515ffd34ed5848f2dec4612fdd7
ACR-78505a7dda0b4401a69aba6c586fab82
ACR-3c2f07c4b4ea4ee9908e86737167deb7
   */
  private final Integer percentage;

  public ProgressUpdateNotification(@Nullable String message, @Nullable Integer percentage) {
    this.message = message;
    this.percentage = percentage;
  }

  @CheckForNull
  public String getMessage() {
    return message;
  }

  @CheckForNull
  public Integer getPercentage() {
    return percentage;
  }
}
