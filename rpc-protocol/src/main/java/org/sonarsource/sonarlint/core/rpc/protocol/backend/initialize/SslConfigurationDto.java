/*
ACR-71dd1e99c35447eebd0b90e831f852bc
ACR-56a54f95ebe34f98a87eef83d9549fb2
ACR-bcfa1641714c41c1880f20db5211c594
ACR-7ddd13f837774db790a1c71ebcffd128
ACR-393e0bf19aea483ba0d1b8c63cd9864a
ACR-bd05a369e10a48a6aac620bb2e1c81f3
ACR-1b0da98163844a50865afbb0d1427cf1
ACR-8e8a900f191f4766b1064fc887371265
ACR-2a96fb59ee5f4e18bdb4844a5072c9b6
ACR-626e73e31c064f37b2216b5520594b77
ACR-a3adf673acae446aa4e8bb41240ee000
ACR-9697e6c60e324b4696cff7ca4d276cb1
ACR-fbf70b026e98474ab797508739c9e141
ACR-6becaf82fa7b4a13b3cec6cae4c6f8fc
ACR-1cbca742018b4f5182c4287443514f3e
ACR-f4ec5b080b384fcbabc72093628c6ca7
ACR-41f850babb384bdb9363b9fa4b2bbda4
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class SslConfigurationDto {

  public static SslConfigurationDto defaultConfig() {
    return new SslConfigurationDto(null, null, null, null, null, null);
  }

  private final Path trustStorePath;
  private final String trustStorePassword;
  private final String trustStoreType;
  private final Path keyStorePath;
  private final String keyStorePassword;
  private final String keyStoreType;

  public SslConfigurationDto(@Nullable Path trustStorePath, @Nullable String trustStorePassword, @Nullable String trustStoreType, @Nullable Path keyStorePath,
    @Nullable String keyStorePassword, @Nullable String keyStoreType) {
    this.trustStorePath = trustStorePath;
    this.trustStorePassword = trustStorePassword;
    this.trustStoreType = trustStoreType;
    this.keyStorePath = keyStorePath;
    this.keyStorePassword = keyStorePassword;
    this.keyStoreType = keyStoreType;
  }

  @CheckForNull
  public Path getTrustStorePath() {
    return trustStorePath;
  }

  @CheckForNull
  public String getTrustStorePassword() {
    return trustStorePassword;
  }

  @CheckForNull
  public String getTrustStoreType() {
    return trustStoreType;
  }

  @CheckForNull
  public Path getKeyStorePath() {
    return keyStorePath;
  }

  @CheckForNull
  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  @CheckForNull
  public String getKeyStoreType() {
    return keyStoreType;
  }
}
