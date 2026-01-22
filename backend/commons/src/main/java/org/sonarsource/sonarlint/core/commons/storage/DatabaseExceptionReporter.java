/*
ACR-c2ff5a1db9f848d5913fdf875fb5f671
ACR-56a18085a3204a249a6b4975f38ac291
ACR-27481da2788f42a7a761da4b161aa3f0
ACR-8b18b69fd1cd4545ae3d69d9a60f87b8
ACR-71e5bc67c1214c468d3e5f10f203a370
ACR-fba13dac6f814185b67f9071582c4e65
ACR-51790ee755264ac68f85f5189fc52fd0
ACR-5d55fc9249df4b8caf71c094ccc1f71b
ACR-2df9792f13684f16a27a9a425d4426a4
ACR-4ff37e72092048e780af9227634d86b2
ACR-6038392b71f54a18a4fe3b411e51a559
ACR-70a265bba56b4cdc8ee3627513ecf826
ACR-069b07fd9abd44db99b5bc45480723b1
ACR-4260ed0e0593449bb608351cbfa7a1d1
ACR-b973cdf7f5d942b5a21034aa91053063
ACR-0647260d4a14455d98642a10d9d648ee
ACR-d33232b3460a46a3addb87d612198371
 */
package org.sonarsource.sonarlint.core.commons.storage;

import io.sentry.Sentry;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-e52923c3c7584d9c9165cc873faa7b8b
ACR-4706d32d8aca404f83c33bb7e2a1636b
ACR-3f9bf70019134a7489f0a8d425a6d9f6
 */
public final class DatabaseExceptionReporter {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  static final String DEDUP_WINDOW_PROPERTY = "sonarlint.internal.db.dedupWindowMs";
  private static final long DEFAULT_DEDUP_WINDOW_MS = 60 * 60 * 1000L; //ACR-f45290956c314d81986c93daad2339f2

  private static final Map<Integer, Long> recentMessageHashes = new ConcurrentHashMap<>();

  private DatabaseExceptionReporter() {
  }

  /*ACR-fa69e8a6a0fc47709bf611cb5f1d0352
ACR-ef62ba3e56e24f52a06fda0af63f071a
ACR-a4d478a29e494fa2bb906eb640d2489b
ACR-b1865e4de8f941d0a7d877d475ff10a2
ACR-f97d96981a8e498db410473460c10164
ACR-176cc4602ace4932b6d9986f0499256e
ACR-be56fc3a52264d939168825d2418ee17
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
        //ACR-9d9771f660de45f59fccfa30b36b4b68
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

  //ACR-1fd7b08666344d748e056c81a0aeb891
  static void clearRecentExceptions() {
    recentMessageHashes.clear();
  }

  //ACR-3ca32238f7e94fa4b54c849c51d5a512
  static int getRecentExceptionsCount() {
    return recentMessageHashes.size();
  }
}
