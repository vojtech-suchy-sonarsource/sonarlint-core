/*
ACR-c18859d02a9141b7b2277565ed658bb0
ACR-7323df398ed7427fa347a4fd32c86f0f
ACR-aeba192fc92449c48019f9c77d16afa2
ACR-8c71fe0a95774b36993e716f4d7ed6a8
ACR-099e43091b2b4dc9bbe1d2eafbebfb8b
ACR-2082af6743ff4bceb45a51c00f96dde3
ACR-923e7ea93a844dd7991624240c6266dc
ACR-6cd9a7467d9e474bb69224a64f536892
ACR-269a2b5e35744398bc1877657454fb6f
ACR-a041529070b349a3bc85e7d78ebf10d8
ACR-b64f7d2e85d044f2a09e31d04e0f8696
ACR-2e89f691b5874881953c89efc935c59e
ACR-4944560845c749669f52a5208572eb0f
ACR-4e660fb03c58447791809f01dc764dc2
ACR-b856651305554b1c9b8cdb0501440bf4
ACR-2af15da2ff5c489abe8a00276cd6377b
ACR-49fbdaa49b5b4efe9a33698a606a2b1f
 */
package org.sonarsource.sonarlint.core.http.ssl;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class SslConfig {
  private final CertificateStore keyStore;
  private final CertificateStore trustStore;

  public SslConfig(@Nullable CertificateStore keyStore, @Nullable CertificateStore trustStore) {
    this.keyStore = keyStore;
    this.trustStore = trustStore;
  }

  @CheckForNull
  public CertificateStore getKeyStore() {
    return keyStore;
  }

  @CheckForNull
  public CertificateStore getTrustStore() {
    return trustStore;
  }
}
