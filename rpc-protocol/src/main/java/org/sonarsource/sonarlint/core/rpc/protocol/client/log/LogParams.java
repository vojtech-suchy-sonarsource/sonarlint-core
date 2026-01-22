/*
ACR-46cfc6d974484a249536e36027699bea
ACR-64e79acd58ca4f24804dc6645ef5fecf
ACR-1abe95ba09ca46a096ee1544c1261312
ACR-2ae2e68d3b2c4ebda3548f760715b8ec
ACR-a46db01bc169498eb2c045e015021b46
ACR-97868bbf398a4913b49aecf113597708
ACR-dd2e02948c1946bfb88767198a80dd52
ACR-e0fb4b380dc642529151f2ed6f35840e
ACR-3a95eb2875744edcb890cc2f37a4f25b
ACR-89e0e27903214b58963f8f1ca6959172
ACR-307ddd0a4386479fb6247a209e5b5dc2
ACR-52090107c5d74290b73822932808a396
ACR-72914aa126384455b0b3e63e164717cc
ACR-56152aa2f1784e4cb6e9d282177566f3
ACR-ea03795f18b449e59fd7a541387b1d1b
ACR-fe29485e02864b0abb40abe87f69bd92
ACR-4875156326ff46878c9c98305d1f91ab
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.log;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class LogParams {

  private final LogLevel level;
  @Nullable
  private final String message;
  @Nullable
  private final String configScopeId;
  private final String threadName;
  private final String loggerName;
  @Nullable
  private final String stackTrace;
  private final Instant loggedAt;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault());

  public LogParams(LogLevel level, @Nullable String message, @Nullable String configScopeId, @Nullable String stackTrace, Instant loggedAt) {
    this(level, message, configScopeId, Thread.currentThread().getName(), "sonarlint", stackTrace, loggedAt);
  }

  public LogParams(LogLevel level, @Nullable String message, @Nullable String configScopeId, String threadName, String loggerName, @Nullable String stackTrace, Instant loggedAt) {
    this.level = level;
    this.message = message;
    this.configScopeId = configScopeId;
    this.threadName = threadName;
    this.loggerName = loggerName;
    this.stackTrace = stackTrace;
    this.loggedAt = loggedAt;
  }

  public LogLevel getLevel() {
    return level;
  }

  @CheckForNull
  public String getMessage() {
    return message;
  }

  /*ACR-8a2cbe64b8c344878d8f7632a3f78129
ACR-9c9493dd1d4c4b06b9250a71e4b01934
ACR-64819b327c17426e8d32526534f9dfdc
   */
  @CheckForNull
  public String getConfigScopeId() {
    return configScopeId;
  }

  public String getThreadName() {
    return threadName;
  }

  public String getLoggerName() {
    return loggerName;
  }

  @CheckForNull
  public String getStackTrace() {
    return stackTrace;
  }

  public Instant getLoggedAt() {
    return loggedAt;
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(" [");
    sb.append(formatter.format(loggedAt));
    sb.append("] [");
    sb.append(threadName);
    sb.append("] ");
    sb.append(level.toString());
    sb.append(" ");
    sb.append(loggerName);
    sb.append(" - ");
    sb.append(message);
    if (stackTrace != null) {
      sb.append(System.lineSeparator());
      sb.append(stackTrace);
    }
    return sb.toString();
  }
}
