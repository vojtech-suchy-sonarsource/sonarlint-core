/*
ACR-35aabec79f1f4f9ea3ae27ba1d8b031b
ACR-f4dd3e5f35a34c919abfe577ef63a5cb
ACR-df3ba3e65cb741c5928d99d719cb633e
ACR-1cbef65196ab46768a05f8d3acc40775
ACR-d64346ac91b644559c1063ea76ea2919
ACR-2f41e47489e14969b6b707c7355ef999
ACR-de3ad04a89f0456dac8d3cd7e23d5953
ACR-15f49117b9b44b90b1ce086c69d56cba
ACR-f1fd53606846415db35330a523eb4fc0
ACR-d76d79689a52420c9a863e9ce8a890fd
ACR-d14b59cd6eb448398e98b76d1f0983ad
ACR-aa71cea6b66f4d4dbc5924d3e6d0a7ca
ACR-406ae4efdeea410cb6524672240d36f0
ACR-de2a3ce1bd6b4529b604290373a4be81
ACR-3714433c1bb24a62acf84e570ffa407d
ACR-dd26bbd51c194c14adf07e0eb652aed5
ACR-ca2de4b32bea46adbea39c4c38778ff4
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

    //ACR-dc323c4d00524789bcd4c20d0c2e3287
    sentryMock.verify(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)), times(1));
  }
}
