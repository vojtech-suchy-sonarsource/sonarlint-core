/*
ACR-d0808e8f6c834ed79e821e3963c3b062
ACR-a86df19392d248be9f0c6733c4949a12
ACR-078c814917884fc1906fa5bbd85bfd66
ACR-6143db14c3834a70942cc18ad76c7eeb
ACR-0ac9b62663dd446eaafa31a04575701d
ACR-a2a323ba9b874800b99f036b11d3cd67
ACR-fa23faada6c54777aa5e747ce3e957cb
ACR-aff09f18b37546d9af9ff979ecab9698
ACR-4a9070242a9a4517aea34da89b826be2
ACR-d3aa6239139a4b78b008a2f83b33aeaa
ACR-5463eeffbe20434e85890ebabc4f5cbb
ACR-97d20734e189493cb509815a5e883cef
ACR-371b39c36635421c97457ea028df4984
ACR-28396b5a32d8424e8defff5f4c19570c
ACR-9165615f5ba748fdaa75e95d45c583a7
ACR-0b519a2288e049dda3fee5b8f1ab76f5
ACR-5a9691ec188a42eeb39b01d476ef8835
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
