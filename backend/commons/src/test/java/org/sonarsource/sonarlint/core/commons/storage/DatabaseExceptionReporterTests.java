/*
ACR-d05b9b8591224d0bb8265df0887303fb
ACR-895009760dc445a58a548dcd8b48c01e
ACR-71400cb73c944029af2d902b44fe99ac
ACR-bcb59bf6233d4611a1f00b20a3bcac69
ACR-cb60a58098544b37b61e9bfeb7360bf5
ACR-1c58bfebf1754051a1997fe67fa55842
ACR-6e384b7977314dc6959fa75aecb6835a
ACR-3e132833f07042059a53161ce774d075
ACR-c390acedea8d477f8a630c76c95ebb12
ACR-47e13ceac2ec44148889ee1666cc7441
ACR-fa8b837beeab4e38a5122ba15db4a767
ACR-39f992eb205b493aa88877b0f8fb7ab7
ACR-bcd37284f5ae4c98a50a915a7c40479c
ACR-5365782227684065afb24abe105857c3
ACR-0a0d1fdbe3d541a8ab4928fb24654199
ACR-5032c697027642b281b2cbb90001b44e
ACR-cd39a9e859b447649e0e9ad679501458
 */
package org.sonarsource.sonarlint.core.commons.storage;

import io.sentry.IScope;
import io.sentry.Sentry;
import io.sentry.ScopeCallback;
import io.sentry.logger.ILoggerApi;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

class DatabaseExceptionReporterTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private MockedStatic<Sentry> sentryMock;

  @BeforeEach
  void setUp() {
    DatabaseExceptionReporter.clearRecentExceptions();
    sentryMock = mockStatic(Sentry.class);
    sentryMock.when(Sentry::logger).thenReturn(mock(ILoggerApi.class));
  }

  @AfterEach
  void tearDown() {
    sentryMock.close();
    DatabaseExceptionReporter.clearRecentExceptions();
    System.clearProperty(DatabaseExceptionReporter.DEDUP_WINDOW_PROPERTY);
  }

  @Test
  void should_capture_generic_exception() {
    var exception = new RuntimeException("Test database error");

    DatabaseExceptionReporter.capture(exception, "runtime", "jooq.execute", "SELECT * FROM test");

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));
    assertThat(exceptionCaptor.getValue()).isSameAs(exception);
    assertThat(exceptionCaptor.getValue().getMessage()).isEqualTo("Test database error");
  }

  @Test
  void should_capture_sql_exception_with_details() {
    var sqlException = new SQLException("SQL error", "42000", 1234);

    DatabaseExceptionReporter.capture(sqlException, "startup", "flyway.migrate", null);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));
    assertThat(exceptionCaptor.getValue()).isInstanceOf(SQLException.class);
    assertThat(((SQLException) exceptionCaptor.getValue()).getSQLState()).isEqualTo("42000");
  }

  @Test
  void should_capture_exception_without_sql() {
    var exception = new RuntimeException("Pool creation failed");

    DatabaseExceptionReporter.capture(exception, "startup", "h2.pool.create");

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));
    assertThat(exceptionCaptor.getValue()).isSameAs(exception);
  }

  @Test
  void should_deduplicate_same_message_within_window() {
    var exception = new RuntimeException("Duplicate error");

    DatabaseExceptionReporter.capture(exception, "runtime", "jooq.execute", "SELECT 1");
    DatabaseExceptionReporter.capture(exception, "runtime", "jooq.execute", "SELECT 1");

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)), times(1));
    assertThat(exceptionCaptor.getValue()).isSameAs(exception);
  }

  @Test
  void should_not_deduplicate_different_exceptions() {
    var exception1 = new RuntimeException("Error 1");
    var exception2 = new RuntimeException("Error 2");

    DatabaseExceptionReporter.capture(exception1, "runtime", "jooq.execute", "SELECT 1");
    DatabaseExceptionReporter.capture(exception2, "runtime", "jooq.execute", "SELECT 2");

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)), times(2));
    assertThat(exceptionCaptor.getAllValues()).containsExactly(exception1, exception2);
  }

  @Test
  void should_deduplicate_same_message_even_with_different_phase() {
    var exception = new RuntimeException("Same error");

    DatabaseExceptionReporter.capture(exception, "startup", "h2.pool.create");
    DatabaseExceptionReporter.capture(exception, "shutdown", "h2.pool.dispose");

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)), times(1));
    assertThat(exceptionCaptor.getValue()).isSameAs(exception);
  }

  @Test
  void should_always_report_null_message_exceptions_without_deduplication() {
    var exception1 = new RuntimeException();
    var exception2 = new RuntimeException();

    DatabaseExceptionReporter.capture(exception1, "runtime", "jooq.execute");
    DatabaseExceptionReporter.capture(exception2, "runtime", "jooq.execute");

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)), times(2));
    assertThat(exceptionCaptor.getAllValues()).containsExactly(exception1, exception2);
  }

  @Test
  void should_truncate_long_sql() {
    var longSql = "SELECT " + "a".repeat(2000) + " FROM test";
    var exception = new RuntimeException("SQL error");

    DatabaseExceptionReporter.capture(exception, "runtime", "jooq.execute", longSql);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));
    assertThat(exceptionCaptor.getValue()).isSameAs(exception);
  }

  @Test
  void should_cleanup_old_entries_after_dedup_window() {
    System.setProperty(DatabaseExceptionReporter.DEDUP_WINDOW_PROPERTY, "50");

    DatabaseExceptionReporter.capture(new RuntimeException("Error 1"), "runtime", "op1");
    DatabaseExceptionReporter.capture(new RuntimeException("Error 2"), "runtime", "op2");
    DatabaseExceptionReporter.capture(new RuntimeException("Error 3"), "runtime", "op3");

    assertThat(DatabaseExceptionReporter.getRecentExceptionsCount()).isEqualTo(3);

    await().atLeast(java.time.Duration.ofMillis(100)).untilAsserted(() -> {
      DatabaseExceptionReporter.capture(new RuntimeException("Error 4"), "runtime", "op4");
      assertThat(DatabaseExceptionReporter.getRecentExceptionsCount()).isEqualTo(1);
    });
  }

  @Test
  void should_set_scope_tags_for_generic_exception() {
    var scope = mock(IScope.class);
    sentryMock.when(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)))
      .thenAnswer(invocation -> {
        var callback = invocation.getArgument(1, ScopeCallback.class);
        callback.run(scope);
        return null;
      });

    var exception = new RuntimeException("Test error");
    DatabaseExceptionReporter.capture(exception, "startup", "h2.pool.create");

    verify(scope).setTag("component", "database");
    verify(scope).setTag("db.phase", "startup");
    verify(scope).setTag("db.operation", "h2.pool.create");
  }

  @Test
  void should_set_scope_tags_for_sql_exception_with_sql_state() {
    var scope = mock(IScope.class);
    sentryMock.when(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)))
      .thenAnswer(invocation -> {
        var callback = invocation.getArgument(1, ScopeCallback.class);
        callback.run(scope);
        return null;
      });

    var sqlException = new SQLException("SQL error", "42000", 1234);
    DatabaseExceptionReporter.capture(sqlException, "runtime", "jooq.execute");

    verify(scope).setTag("component", "database");
    verify(scope).setTag("db.phase", "runtime");
    verify(scope).setTag("db.operation", "jooq.execute");
    verify(scope).setTag("db.sqlState", "42000");
    verify(scope).setTag("db.errorCode", "1234");
  }

  @Test
  void should_not_set_sql_state_tag_when_null() {
    var scope = mock(IScope.class);
    sentryMock.when(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)))
      .thenAnswer(invocation -> {
        var callback = invocation.getArgument(1, ScopeCallback.class);
        callback.run(scope);
        return null;
      });

    var sqlException = new SQLException("SQL error", null, 5678);
    DatabaseExceptionReporter.capture(sqlException, "runtime", "jooq.execute");

    verify(scope).setTag("component", "database");
    verify(scope).setTag("db.errorCode", "5678");
    verify(scope, times(0)).setTag("db.sqlState", null);
  }

  @Test
  void should_set_sql_extra_when_provided() {
    var scope = mock(IScope.class);
    sentryMock.when(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)))
      .thenAnswer(invocation -> {
        var callback = invocation.getArgument(1, ScopeCallback.class);
        callback.run(scope);
        return null;
      });

    var exception = new RuntimeException("Test error");
    DatabaseExceptionReporter.capture(exception, "runtime", "jooq.execute", "SELECT * FROM test");

    verify(scope).setExtra("db.sql", "SELECT * FROM test");
  }

  @Test
  void should_not_set_sql_extra_when_empty() {
    var scope = mock(IScope.class);
    sentryMock.when(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)))
      .thenAnswer(invocation -> {
        var callback = invocation.getArgument(1, ScopeCallback.class);
        callback.run(scope);
        return null;
      });

    var exception = new RuntimeException("Test error");
    DatabaseExceptionReporter.capture(exception, "runtime", "jooq.execute", "");

    verify(scope, times(0)).setExtra(any(), any());
  }

  @Test
  void should_truncate_sql_in_extra_when_exceeds_1000_chars() {
    var scope = mock(IScope.class);
    sentryMock.when(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)))
      .thenAnswer(invocation -> {
        var callback = invocation.getArgument(1, ScopeCallback.class);
        callback.run(scope);
        return null;
      });

    var longSql = "SELECT " + "a".repeat(2000) + " FROM test";
    var exception = new RuntimeException("SQL error");
    DatabaseExceptionReporter.capture(exception, "runtime", "jooq.execute", longSql);

    var sqlCaptor = ArgumentCaptor.forClass(String.class);
    verify(scope).setExtra(any(), sqlCaptor.capture());
    assertThat(sqlCaptor.getValue()).hasSize(1000 + "... [truncated]".length());
    assertThat(sqlCaptor.getValue()).endsWith("... [truncated]");
  }

  @Test
  void should_use_default_dedup_window_when_property_is_invalid() {
    System.setProperty(DatabaseExceptionReporter.DEDUP_WINDOW_PROPERTY, "not-a-number");

    var exception1 = new RuntimeException("Error");
    var exception2 = new RuntimeException("Error");

    DatabaseExceptionReporter.capture(exception1, "runtime", "op1");
    DatabaseExceptionReporter.capture(exception2, "runtime", "op2");

    //ACR-d648115d5c944362bf8a8bf057e12f4e
    sentryMock.verify(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)), times(1));
  }
}
