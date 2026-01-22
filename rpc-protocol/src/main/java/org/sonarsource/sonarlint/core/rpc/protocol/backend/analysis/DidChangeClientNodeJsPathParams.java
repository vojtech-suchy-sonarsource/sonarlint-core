/*
ACR-83cf1cc044ca49109f655db9b01b00f5
ACR-bf66646575ed4941a27caf4182fc315b
ACR-f6469d2d97ea472bb0822dce03122acf
ACR-bc7f78748b2041dcae958e9c3055eea7
ACR-b4afb98cf49e489dba6be8f09e10e821
ACR-9b308f7d3f6f4b6bb692b7dd2fed8872
ACR-a0c0f8f2fd474489857556ed8377e949
ACR-0ab14fb9825745058c3bf0bb57d88447
ACR-5ca3cc42e7a64d019e4672464ec4a80f
ACR-ad5072d0cd564942958a47fa84d39500
ACR-e70443806a7443f3801d7415e0580f08
ACR-080cdf3877a24aaeb2d9046f941b32a1
ACR-2befcadc9dba4e3caa4163ab9df018e6
ACR-193c5b4fa8c94c1b8ca9bc28caf3c747
ACR-d99bae6e44de4207b845a78d27430dcb
ACR-e745b1ef9c1a4870a39856d50c179e84
ACR-693c34cd30eb45cdb7e6999ad2ea547d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class DidChangeClientNodeJsPathParams {

  private final Path clientNodeJsPath;

  public DidChangeClientNodeJsPathParams(@Nullable Path clientNodeJsPath) {
    this.clientNodeJsPath = clientNodeJsPath;
  }

  @CheckForNull
  public Path getClientNodeJsPath() {
    return clientNodeJsPath;
  }
}
