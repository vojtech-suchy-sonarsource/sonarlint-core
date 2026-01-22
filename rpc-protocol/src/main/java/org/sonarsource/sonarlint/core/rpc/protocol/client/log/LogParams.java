/*
ACR-5bd17d0925634e92957f06f9a6129ba7
ACR-85e4f50042d74a1cbefc08366028c193
ACR-d0775c72b05d44d7bc2b5645f5233845
ACR-ee7ac709279f40b591dc38bb7ae61833
ACR-aad905cc0aa5444eb2cdc169c3653de1
ACR-d80dcd1c49c04dacabe7d0fa160bf776
ACR-2b8e2f2911bb4714acbfc5f17f763ac5
ACR-5b1561ab75b6422f927d8626510ffeba
ACR-7f724076aafc4bb7aa75c3987e8ead52
ACR-2dfb45dfe3be4865be74c38290d43cad
ACR-c26bf6825d9f4182bf2d640b814e2cc1
ACR-b1c88f51417d48cfba2c8fcea6790da6
ACR-816e62d28054455f8500ceb86cbb1bd6
ACR-acd5ff1aa4874330bd4514cc5a412b3d
ACR-4b7b528828d4465eb844ef6776399648
ACR-7eae756ed8f9451d9cc00ae0076770ee
ACR-8db68261e374467688172ea24a063c86
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

  /*ACR-32dc343dfe8c466eba37f6ba16ca1a11
ACR-ba9c7da7917f4f42ac21fb5a853b3132
ACR-d84ef8cf16df497187f83fe92fdbd206
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
