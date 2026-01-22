/*
ACR-9842b9337443473589da840bc7941cd9
ACR-701238e08d9d4399a7aaf60d9c3e94bb
ACR-c6c4c0abe5b84c46b3793913ecfb1bba
ACR-9c3e2cb61caf49fd9b539e1c7a49ee6c
ACR-18862ce3d78c4936ab6bc2f3a6a55fbb
ACR-e74b67af1c0d4ab698f9aa730d0ceda5
ACR-d2dc2499e47746ddae733980da9503fb
ACR-fd4968335cc44841815b74548020b7f0
ACR-0f9a020eb3604ced94eccbb9d9045fd7
ACR-df4e5c3ad4ec4748aebf9758d2d1061d
ACR-baa6098b9a2a4702aa9f5045ba08b3da
ACR-58eddaa0ed3c401ebd2f873d2276f9ba
ACR-4625f07a188a4adc93ff8425d6003c9e
ACR-6fa95eea9f884c59bcd2e4d5839cdc79
ACR-e0dfd9d904014cc88635e37263af16ee
ACR-9e45e73e44df4b2088cc39506b584fb1
ACR-97f7f80b68bc42beb57ab4d3833e53ee
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.time.Instant;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;

class RpcClientLogOutput implements LogOutput {

  private final SonarLintRpcClient client;

  private final InheritableThreadLocal<String> configScopeId = new InheritableThreadLocal<>();

  RpcClientLogOutput(SonarLintRpcClient client) {
    this.client = client;
  }

  @Override
  public void log(@Nullable String msg, Level level, @Nullable String stacktrace) {
    client.log(new LogParams(LogLevel.valueOf(level.name()), msg, configScopeId.get(), stacktrace, Instant.now()));
  }

  public void setConfigScopeId(@Nullable String configScopeId) {
    this.configScopeId.set(configScopeId);
  }
}
