/*
ACR-b983850dfc914c7991351ea3c584182a
ACR-04cc2a9421804781a2041f235e997c1b
ACR-79a7836877af48d5afbc6389c50007c4
ACR-8d4733b91ac1467ba05162ee184ecb11
ACR-e8f4a5e2102f4924b0016c70c1eff3a6
ACR-2890310ff5fd43f49278b68b64d48030
ACR-995f9ab104634ec8a513aa99b0666fcb
ACR-05efafee40e04fe18a8154efe61693ab
ACR-a895b149bcfc4eaab00918d4c1a83257
ACR-ee74519fcc65418caa079f474234eaa5
ACR-b1d665b85c3940b395e44c55a6f277f5
ACR-a4b32503542b4109885039ea0c352202
ACR-3cd6e959d87247c1b63d3385a8e69f00
ACR-f4cda75ca4b94c8194fb8ec4f6ec613d
ACR-3ff7db8c39674f46bea3a1522ad873b7
ACR-00167dcf05304db0873b4e046ea62c80
ACR-35b6896afac748d0a4525c770a98fa97
 */
package org.sonarsource.sonarlint.core.commons.log;

import io.sentry.Sentry;
import io.sentry.SentryLogLevel;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.LogOutput.Level;

/*ACR-b8456823b08542f380fba4b5eb4ca521
ACR-ab815f04a7af44a18108da013aa2f498
 */
public class SonarLintLogger {
  private static final SonarLintLogger logger = new SonarLintLogger();
  private Level currentLevel = Level.OFF;

  public static SonarLintLogger get() {
    return logger;
  }

  private final InheritableThreadLocal<LogOutput> target = new InheritableThreadLocal<>();

  SonarLintLogger() {
    //ACR-abaafcd2d2ea4203856f19d16b938153
  }

  public void setTarget(@Nullable LogOutput target) {
    this.target.set(target);
  }

  /*ACR-32899201514a4f52bf06124c5b60e4ea
ACR-5067c2365efc423088d4da16a7454da6
ACR-5595a2d5e9114b78b1cba14418a01f5a
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
      //ACR-aa1e044a5bfe49d495eac4227ea0eaf2
      return SentryLogLevel.ERROR;
    }
  }

  /*ACR-dd7061b7c4444e7c9858a6055e96a853
ACR-ed04f0d07a64471e88c8a9be86a56434
   */
  public static String singlePlural(int count, String singular) {
    return count == 1 ? singular : (singular + "s");
  }

  public void setLevel(Level newLevel) {
    this.currentLevel = newLevel;
  }
}
