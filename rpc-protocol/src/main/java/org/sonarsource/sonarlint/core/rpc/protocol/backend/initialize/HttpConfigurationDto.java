/*
ACR-c0890fef44594600a89f69cef62a5ddc
ACR-6de5cd10e18c40f79dad5c07ab71aacc
ACR-46925cf85e0b4ad08162fdb191096585
ACR-2ff02bd7fb5d488da867089d605b34c2
ACR-0693f08580b34ed6973c279cca8de2e7
ACR-322212cf8cb14effa86cd3df54687cdd
ACR-951f6aa965f24640998eadf091ddbef6
ACR-bdce219212d644b1a09952e7a3829a7f
ACR-e18065d7d6f44b8bae07492d5b14e5ad
ACR-6abbb75e48154868a7d1a275a4cdc32a
ACR-8ec86b9461064cd3a064115e7120d0b6
ACR-4e7501ee8ac8472aada909dbd9858e33
ACR-760204c6201344b0ac15ede5f432ee4b
ACR-5107ba91f98f4213ab1fba03f01ff855
ACR-4be54fa233464f4689b84b5a220508cd
ACR-361b8f3c755b4b38b0d3b5b067c7f232
ACR-b3b4e9f091cd415c823e2af7c4caf4db
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
