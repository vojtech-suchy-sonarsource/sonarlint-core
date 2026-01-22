/*
ACR-3bffbe8de62d4177b242410787876110
ACR-c5cf035e830d4223889f9b92d2c9a641
ACR-dcf5539fc367411bb993c019dc586ea4
ACR-2c01e8bd3be2464296e6517846859176
ACR-b6a39bfd314945d18b6f290e5aa14178
ACR-3fdfecea35e345ad89128840aec406e4
ACR-07c2af4f4dfe4370a3c654afae99acc5
ACR-87301c1501ee4d2aa571f1fca69a4912
ACR-a9751aad652243f1abb5aa7c2b0d4dcc
ACR-9f5ad7fc81fb432fb2295332aec66161
ACR-82243bc8086e49df96ff752441e13ae1
ACR-7dbd1f5d5e044dfebba87f98f3b48676
ACR-a4cc64c080f3482e878bba816948e726
ACR-fb5643158e5a4cc8838d19c4a367c8cf
ACR-7e5b8df87c93400aacce6e0e995e1c61
ACR-f3f80cdc5f40452d8157d1cba6d37085
ACR-19b1d8738c264f50a06189caa037c7ad
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.time.Duration;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class HttpConfigurationDto {

  public static HttpConfigurationDto defaultConfig() {
    return new HttpConfigurationDto(SslConfigurationDto.defaultConfig(), null, null, null, null);
  }

  private final SslConfigurationDto sslConfiguration;
  private final Duration connectTimeout;
  private final Duration socketTimeout;
  private final Duration connectionRequestTimeout;
  private final Duration responseTimeout;

  public HttpConfigurationDto(SslConfigurationDto sslConfiguration, @Nullable Duration connectTimeout, @Nullable Duration socketTimeout,
    @Nullable Duration connectionRequestTimeout, @Nullable Duration responseTimeout) {
    this.sslConfiguration = sslConfiguration;
    this.connectTimeout = connectTimeout;
    this.socketTimeout = socketTimeout;
    this.connectionRequestTimeout = connectionRequestTimeout;
    this.responseTimeout = responseTimeout;
  }

  public SslConfigurationDto getSslConfiguration() {
    return sslConfiguration;
  }

  @CheckForNull
  public Duration getConnectTimeout() {
    return connectTimeout;
  }

  @CheckForNull
  public Duration getSocketTimeout() {
    return socketTimeout;
  }

  @CheckForNull
  public Duration getConnectionRequestTimeout() {
    return connectionRequestTimeout;
  }

  @CheckForNull
  public Duration getResponseTimeout() {
    return responseTimeout;
  }
}
