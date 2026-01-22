/*
ACR-c91171a443814d5885ac8fba877bcba7
ACR-40d4d7614fe2401cbd24ef6947e11a29
ACR-414c5243cfee446983cf0f8d7ab4e6c1
ACR-82b7e4735ea14d81afc4a1d449004bb4
ACR-07656257a680402f8a353049e0bc8b21
ACR-5dd328d989c7429aa19e8c95c63f4810
ACR-f3547bd097d54d709ae9f8b3b502b2f1
ACR-bc23ed67137441fd833ec43d100d9149
ACR-a6d3712875dc4bb090a40f6d67347f93
ACR-2d36f44e1fce48a9a820cf2fb112b537
ACR-a14a5a011f1f45a48ec34702c7abb054
ACR-80fc21662541442cad39945736071c83
ACR-d6a3ffbf64ba4abd9f2691c11129fad4
ACR-4aad5e93253949db94401a080e7b01fc
ACR-79b13fea156242fe9bdfa73440efb813
ACR-3fa1675fbd044cd4bbe0ab8cfc3db121
ACR-7d834133bd9148059076707c6a4a1726
 */
package org.sonarsource.sonarlint.core.commons.log;

import io.sentry.Sentry;
import io.sentry.SentryLogLevel;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.LogOutput.Level;

/*ACR-043c209403384c19a4f459089470d57e
ACR-9a2d2691aa5c4b459fdda692e719ca06
 */
public class SonarLintLogger {
  private static final SonarLintLogger logger = new SonarLintLogger();
  private Level currentLevel = Level.OFF;

  public static SonarLintLogger get() {
    return logger;
  }

  private final InheritableThreadLocal<LogOutput> target = new InheritableThreadLocal<>();

  SonarLintLogger() {
    //ACR-c64bf89f20e943cabd66638049b20091
  }

  public void setTarget(@Nullable LogOutput target) {
    this.target.set(target);
  }

  /*ACR-8a17abf60b484abaa3d128c6ade96d7b
ACR-61faeee3b80f4573b043c67984289656
ACR-d19aae7a8cbf449a90bf95c750f501b4
   */
  @CheckForNull
  public LogOutput getTargetForCopy() {
    return this.target.get();
  }

  public void trace(String msg) {
    log(msg, Level.TRACE, (Throwable) null);
  }

  public void trace(String msg, @Nullable Object arg) {
    doLogExtractingThrowable(Level.TRACE, msg, new Object[]{arg});
  }

  public void trace(String msg, @Nullable Object arg1, @Nullable Object arg2) {
    doLogExtractingThrowable(Level.TRACE, msg, new Object[]{arg1, arg2});
  }

  public void trace(String msg, Object... args) {
    doLogExtractingThrowable(Level.TRACE, msg, args);
  }

  public void debug(String msg) {
    log(msg, Level.DEBUG, (Throwable) null);
  }

  public void debug(String msg, @Nullable Object arg) {
    doLogExtractingThrowable(Level.DEBUG, msg, new Object[]{arg});
  }

  public void debug(String msg, @Nullable Object arg1, @Nullable Object arg2) {
    doLogExtractingThrowable(Level.DEBUG, msg, new Object[]{arg1, arg2});
  }

  public void debug(String msg, Object... args) {
    doLogExtractingThrowable(Level.DEBUG, msg, args);
  }

  public void info(String msg) {
    log(msg, Level.INFO, (Throwable) null);
  }

  public void info(String msg, @Nullable Object arg) {
    doLogExtractingThrowable(Level.INFO, msg, new Object[]{arg});
  }

  public void info(String msg, @Nullable Object arg1, @Nullable Object arg2) {
    doLogExtractingThrowable(Level.INFO, msg, new Object[]{arg1, arg2});
  }

  public void info(String msg, Object... args) {
    doLogExtractingThrowable(Level.INFO, msg, args);
  }

  public void warn(String msg) {
    log(msg, Level.WARN, (Throwable) null);
  }

  public void warn(String msg, Throwable thrown) {
    log(msg, Level.WARN, thrown);
  }

  public void warn(String msg, @Nullable Object arg) {
    doLogExtractingThrowable(Level.WARN, msg, new Object[]{arg});
  }

  public void warn(String msg, @Nullable Object arg1, @Nullable Object arg2) {
    doLogExtractingThrowable(Level.WARN, msg, new Object[]{arg1, arg2});
  }

  public void warn(String msg, Object... args) {
    doLogExtractingThrowable(Level.WARN, msg, args);
  }

  public void error(String msg) {
    log(msg, Level.ERROR, (Throwable) null);
  }

  public void error(String msg, @Nullable Object arg) {
    doLogExtractingThrowable(Level.ERROR, msg, new Object[]{arg});
  }

  public void error(String msg, @Nullable Object arg1, @Nullable Object arg2) {
    doLogExtractingThrowable(Level.ERROR, msg, new Object[]{arg1, arg2});
  }

  public void error(String msg, Object... args) {
    doLogExtractingThrowable(Level.ERROR, msg, args);
  }

  public void error(String msg, Throwable thrown) {
    log(msg, Level.ERROR, thrown);
  }

  private void doLogExtractingThrowable(Level level, String msg, Object[] argArray) {
    var tuple = MessageFormatter.arrayFormat(msg, argArray);
    log(tuple.getMessage(), level, tuple.getThrowable());
  }

  private void log(@Nullable String formattedMessage, Level level, @Nullable Throwable t) {
    if (currentLevel.isMoreVerboseOrEqual(level) && (formattedMessage != null || t != null)) {
      var stacktrace = t == null ? null : LogOutput.stackTraceToString(t);
      log(formattedMessage, level, stacktrace);
    }
  }

  private void log(@Nullable String formattedMessage, Level level, @Nullable String stackTrace) {
    var output = Optional.ofNullable(target.get()).orElseThrow(() -> {
      var noLogOutputConfigured = new IllegalStateException("No log output configured");
      noLogOutputConfigured.printStackTrace(System.err);
      return noLogOutputConfigured;
    });
    if (output != null) {
      output.log(formattedMessage, level, stackTrace);
      Sentry.logger().log(getSentryLogLevel(level), formattedMessage);
    }
  }

  private static SentryLogLevel getSentryLogLevel(Level level) {
    try {
      return SentryLogLevel.valueOf(level.name());
    } catch (IllegalArgumentException notSupported) {
      //ACR-56d15b48f65346eb939dcd1d04d53c54
      return SentryLogLevel.ERROR;
    }
  }

  /*ACR-6188e0c8a77541888cc287115c7bce8f
ACR-4371f52be45b4cd09b91260bbe6e98be
   */
  public static String singlePlural(int count, String singular) {
    return count == 1 ? singular : (singular + "s");
  }

  public void setLevel(Level newLevel) {
    this.currentLevel = newLevel;
  }
}
