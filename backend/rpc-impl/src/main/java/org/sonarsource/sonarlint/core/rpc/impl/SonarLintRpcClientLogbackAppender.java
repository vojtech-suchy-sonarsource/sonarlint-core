/*
ACR-4d810d83d5764f1790a7e72e9b2451a1
ACR-5f77137a7c7544b0bc86f8d4aff2c0a6
ACR-13fd5e3630664210a3b55fb15b9771f8
ACR-8d20ccc543a5458c836c51ea591c5eb3
ACR-5f3300f46d2b49c0bf80fcdf1bd9e27e
ACR-1760162eb570468f876870396903a2da
ACR-28a4c9b24dbb451faedf35bf8472973d
ACR-6093884c9a7446b182634200b041d911
ACR-10cf7b40b1c147f09e113dfef0087777
ACR-c2de2786f4e9473696ad65df943a4142
ACR-8a32146ba4d845c69cc8713ef4a86d26
ACR-18cf00255a1846939a4c170389afe77d
ACR-5ddff3a81a7b4b55bc5661319cb16a2d
ACR-7f87290b1ef04cc3807f1c9f54a6da56
ACR-0b8cc7bd74b7491ebd50d03d15ccaaac
ACR-84bc6fd3568c48a7b7a533db6ecd1470
ACR-2bd57594e53044c7a4b36c71a1618247
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
