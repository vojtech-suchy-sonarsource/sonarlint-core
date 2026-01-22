/*
ACR-495afb63766a47d8bc9fb662d43e4b69
ACR-78f4370b2a0541ca9a7f6eb4df7f7323
ACR-330a591739ae4084853466f1c6967f45
ACR-4d3b888188ad434baa577f3243423106
ACR-a5cac8383702486097807252fc6f6bf4
ACR-41363f3eefdb436fa933536190e5454e
ACR-9e8c57578105470186c3bf85bd8853b3
ACR-4a0317a2a6634b5ca4261ba8b91cf424
ACR-cd7ae5953d274e9d91c93704576959d3
ACR-7f0092b3657248a1949afa40ff44d8c7
ACR-2ec7b637bcdf4e93b668beb2370caefd
ACR-7e0349ad18064557b46339fe509d9b16
ACR-a47f87cca7224000a1d562858250896f
ACR-d324248000bd4b568160e2ee4de2df9a
ACR-5dfeef190ee34badb4ccd6dfb6a734f3
ACR-43f203493a214b60ba1af6282e174c0c
ACR-6f723cf1124941ccb994f8eb4743761a
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
