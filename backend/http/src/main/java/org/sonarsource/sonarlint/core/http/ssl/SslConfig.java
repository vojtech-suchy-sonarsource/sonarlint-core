/*
ACR-e4eea63cee4d4d90819a81e0c23d68b2
ACR-580afdbc8dc9400e94c18c0e1e240e41
ACR-b6560e1bf6334058a3e438b966951d63
ACR-5d8bb45e4d344d62841230101ed79f56
ACR-df70b7e6511f4a6f96546c05bef21041
ACR-096ac93fd2e24798bc59c88469432c45
ACR-a8eb6fc3139f4049ac1574e5e9dfac7d
ACR-cc63a4001c1e4fb1b408d2301b5ecfcd
ACR-8a5788cf0fbb405d84dd29d794a8b04a
ACR-8e5d4f53350a4ba49abde6272588a1c2
ACR-c25858d946f14a8bb7ab167c02164945
ACR-d9420dcacce84aa2ac388b6539d955b6
ACR-931d796cb83d48a382175515dab0f568
ACR-e76cc6cf6a344f00a7e0335ad37b2081
ACR-b47295c5d2ab48889542a1953498dbea
ACR-740e311fb9dc4920a4f45b70c3769ede
ACR-96a2c1cfa30341ebbc7e7ae19e4054cf
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
