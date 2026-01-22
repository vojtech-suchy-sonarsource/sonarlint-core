/*
ACR-9d1dc72b03024b21881495588e5bb2d3
ACR-9a5bcc11f90d481d9357e5aba6e03010
ACR-040c49ef66c2435b88071576375cd1dd
ACR-8b12e85d99cf40dc88c70719c6788476
ACR-b2d8eefe1d6d4a3f928cd9a231a8d2cc
ACR-8f7e470a3a76440c9e858b5c46c10b24
ACR-ba5b471cf64341fa97bfefd01c5b04f4
ACR-7f6c817c399a44b785611bf89b1e185e
ACR-e5049eb23af546c8ae5c4e93fd5e4e03
ACR-920a975fffe2428b95fea72bac99e7a9
ACR-6586336649f9423da503ca1d9445978a
ACR-3502bcd43fca40908ba50316cce0cc67
ACR-638ca0abc0124d40bd7ced3e18cd0ea1
ACR-621a9154ded04abf9ee251482201e18b
ACR-ca0059757d7440889dd6e493d81a42f7
ACR-753d90ad2cdb45679e85d0b574f7657d
ACR-e30ca36107fc4d97b6e89f8b722cd3ac
 */
package org.sonarsource.sonarlint.core.commons.storage;

import io.sentry.Sentry;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-a0a23a9a588c421d9ad44f3351bfae7e
ACR-95eea2ab35744868a778e9e6c4f87f5b
ACR-0f2635271ac74d599d251e2c41de93ed
 */
public final class DatabaseExceptionReporter {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  static final String DEDUP_WINDOW_PROPERTY = "sonarlint.internal.db.dedupWindowMs";
  private static final long DEFAULT_DEDUP_WINDOW_MS = 60 * 60 * 1000L; //ACR-fa23afd45b854214976f86f94eb8c167

  private static final Map<Integer, Long> recentMessageHashes = new ConcurrentHashMap<>();

  private DatabaseExceptionReporter() {
  }

  /*ACR-4ace8b75b6f84e68bd23f30b7fb0b814
ACR-62630448f2fe4c3e876919d80ddecf82
ACR-06440aa9e9e64183bcfac830268c38bf
ACR-6a715b0427ed4e7aa2d7c89ab220ee66
ACR-1e57fccd85914852965ce8086e9f2129
ACR-f30a675bcd0a4b228ae889d8d089cbae
ACR-ac55b007731a49e5afc7a580dfbb7826
   */
  public static void capture(Throwable exception, String phase, String operation, @Nullable String sql) {
    var message = exception.getMessage();

    if (message != null && isDuplicate(message.hashCode())) {
      LOG.debug("Skipping duplicate database exception report: {} / {}", phase, operation);
      return;
    }

    LOG.debug("Reporting database exception to Sentry: {} / {}", phase, operation);

    Sentry.captureException(exception, scope -> {
      scope.setTag("component", "database");
      scope.setTag("db.phase", phase);
      scope.setTag("db.operation", operation);

      if (exception instanceof SQLException sqlException) {
        var sqlState = sqlException.getSQLState();
        var errorCode = sqlException.getErrorCode();
        if (sqlState != null) {
          scope.setTag("db.sqlState", sqlState);
        }
        scope.setTag("db.errorCode", String.valueOf(errorCode));
      }

      if (sql != null && !sql.isEmpty()) {
        scope.setExtra("db.sql", truncateSql(sql));
      }
    });

    if (message != null) {
      recordException(message.hashCode());
    }
  }

  public static void capture(Throwable exception, String phase, String operation) {
    capture(exception, phase, operation, null);
  }

  private static boolean isDuplicate(int messageHash) {
    var now = System.currentTimeMillis();
    cleanupOldEntries(now);
    var lastReported = recentMessageHashes.get(messageHash);
    return lastReported != null;
  }

  private static void recordException(int messageHash) {
    recentMessageHashes.put(messageHash, System.currentTimeMillis());
  }

  private static void cleanupOldEntries(long now) {
    recentMessageHashes.entrySet().removeIf(entry -> (now - entry.getValue()) > getDedupWindowMs());
  }

  private static long getDedupWindowMs() {
    var property = System.getProperty(DEDUP_WINDOW_PROPERTY);
    if (property != null) {
      try {
        return Long.parseLong(property);
      } catch (NumberFormatException e) {
        //ACR-80fa003e7b6e41beb095f0dce60daaf8
      }
    }
    return DEFAULT_DEDUP_WINDOW_MS;
  }

  private static String truncateSql(String sql) {
    var maxLength = 1000;
    if (sql.length() <= maxLength) {
      return sql;
    }
    return sql.substring(0, maxLength) + "... [truncated]";
  }

  //ACR-760dca9f98974712b2bb3142e84817d8
  static void clearRecentExceptions() {
    recentMessageHashes.clear();
  }

  //ACR-45da818883b14b56b0d363032d9af082
  static int getRecentExceptionsCount() {
    return recentMessageHashes.size();
  }
}
