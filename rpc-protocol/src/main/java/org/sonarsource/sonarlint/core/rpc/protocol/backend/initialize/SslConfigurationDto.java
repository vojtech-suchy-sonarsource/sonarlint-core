/*
ACR-41c9c3c37e114f139fa69e4ef0381e10
ACR-35d2cd3591004cd7a943141aa7be89a7
ACR-59f9c58125574541987ae41ef5af2794
ACR-be619cf416c14ac6ba03d44dbf1025a4
ACR-928f0a84c3b241e88473597856ad8b4a
ACR-0e01dd1e58b644a686b4038e38f70701
ACR-48b55dea4d274dffab3ad5858c5b95e5
ACR-1758e98c89b84d97b5e5925bcdd1d41e
ACR-4af9504cab504d7ebf5cd0d7da881c7a
ACR-eaf33f8ebb72430fbc9fbc5220d91b5c
ACR-271fefc981314641a8685a9903f85ddd
ACR-1c5e4f53fcdd445e9d723f3d7a87d07e
ACR-db877e59571745a0bd01c3d6721f41e8
ACR-71ff6955c813428dbe9de371616afd31
ACR-96635b26d3264c36bd2298edd5c0eedd
ACR-5aef710701224fa282965ef2dcc75098
ACR-895bff0d4be147f98705141e8bbd3969
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
