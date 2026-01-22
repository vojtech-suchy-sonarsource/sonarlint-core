/*
ACR-73ea7218e850466ea538c5636f39de89
ACR-13a832d028af4153a137d507f81f8fdd
ACR-7020d310d6a742d8b73c8af44afbf410
ACR-744224375a8d4ceaa9106fab0d6da362
ACR-d40ca937d0e3496391a76f0ae5a835dc
ACR-a723942fdb0a41d48d88372d70706a2c
ACR-4c5f91c013b0444e9b55167fb9e5f1e3
ACR-0baef00164a04e28aff0095c8bb41c69
ACR-fa7e8a319ee04dd4bc44a62804591fbb
ACR-0392f3ffa55c493cbfbf38eced056ebe
ACR-c3ce93839ec8487c82d512f815082478
ACR-bcff6dd576bf4040bb6bd4a94be2940e
ACR-886b92c9ea784d6d8d4c2e9841d455ad
ACR-de9dad9b170a486399c4992e378101d7
ACR-723288f97ed74b8ca2e40575541061d3
ACR-0541b6c8a67745838873eb00c8891d5f
ACR-0a820a9bac2f4fe580de3d2676bd591b
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import java.time.Instant;
import org.sonarsource.sonarlint.core.SonarLintMDC;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;

class SonarLintRpcClientLogbackAppender extends AppenderBase<ILoggingEvent> {

  private final SonarLintRpcClient rpcClient;
  private final ThrowableProxyConverter tpc = new ThrowableProxyConverter();

  public SonarLintRpcClientLogbackAppender(SonarLintRpcClient client) {
    rpcClient = client;
  }

  @Override
  public void start() {
    tpc.start();
    super.start();
  }

  @Override
  protected void append(ILoggingEvent eventObject) {
    var configScopeId = eventObject.getMDCPropertyMap().get(SonarLintMDC.CONFIG_SCOPE_ID_MDC_KEY);
    var threadName = eventObject.getThreadName();
    var loggerName = eventObject.getLoggerName();
    var formattedMessage = eventObject.getFormattedMessage();
    var loggedAt = Instant.ofEpochMilli(eventObject.getTimeStamp());
    IThrowableProxy tp = eventObject.getThrowableProxy();
    String stackTrace = null;
    if (tp != null) {
      stackTrace = tpc.convert(eventObject);
    }
    rpcClient.log(new LogParams(LogLevel.valueOf(eventObject.getLevel().levelStr), formattedMessage, configScopeId, threadName, loggerName, stackTrace, loggedAt));
  }

}
