/*
ACR-4bd87e4c0115496d812c0c0ec4193d6d
ACR-ee2fbeeefa1b4d908bccc93afde0836d
ACR-1d0eddb14b8947c6a89ce58b06630932
ACR-4cda566a7aed4c82bec0e894012dd9e7
ACR-cb057cd6b15c4bb1b294579ff54c5254
ACR-914195a32b0a41dc894caab25918494e
ACR-a24f60c2227e49e48a6c667279410fa1
ACR-57ecfaecb2d34f14b21ea8f7a0fc4ef9
ACR-a8fc5df0555f47b6acedd82e6a1645cb
ACR-f5030575e96740cc8ef54125c6b657b4
ACR-5920be8a753544f58214f726034efbc9
ACR-d71130b608b347cd86cd6b2cfbe13bb1
ACR-4698ab50124d4e4e9345a1ec61197ffa
ACR-ef531604639e41f591f95c5468ac3f25
ACR-bbdb1f011fa647dcb023352542d42f30
ACR-0799fdee24cf4f939e3b01a4871af67d
ACR-27d64d79c9e24843a824702576d65b75
 */
package org.sonarsource.sonarlint.core.http.ssl;

import java.nio.file.Path;

public class CertificateStore {
  public static final String DEFAULT_PASSWORD = "sonarlint";
  public static final String DEFAULT_STORE_TYPE = "PKCS12";
  private final Path path;
  private final String keyStorePassword;
  private final String keyStoreType;

  public CertificateStore(Path path, String keyStorePassword, String keyStoreType) {
    this.path = path;
    this.keyStorePassword = keyStorePassword;
    this.keyStoreType = keyStoreType;
  }

  public Path getPath() {
    return path;
  }

  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  public String getKeyStoreType() {
    return keyStoreType;
  }
}
