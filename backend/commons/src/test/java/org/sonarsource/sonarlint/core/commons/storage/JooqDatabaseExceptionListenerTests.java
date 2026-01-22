/*
ACR-5c8f04abc04f41ecbd464e43bc999591
ACR-98a1e807b0b5436ca92d54f095a3bf45
ACR-e8f6ac0100914a46b729a787fd843f9f
ACR-52b99e23783745c3b4da45b707af3f52
ACR-96ab0a98c60d45b4856ca99d2c5633fe
ACR-02191c1b42e5419cb48f9df200f91e6f
ACR-7193b88c2bea436f803953e2523906df
ACR-3332c66ff1b840a1aef714ed59fd0dbb
ACR-f0b28c92fc494826846a7fc93ee8b002
ACR-dc3dd4cf1a034e4baf250a25dcb14e45
ACR-b3529f611aff4ac7a4e0eb9f06225780
ACR-2060f036904e4858a15ec2bd383bc39c
ACR-961037933cfb49b594a30b54fddc2dee
ACR-9dc527c749384f9aaecee78628dc8064
ACR-0c2dfd7fb02f43d0a1b05d7a3917fd21
ACR-3c7c629d5800438780ac8c2df55be7f9
ACR-78aea09bb0d04ac9a560778c441de7eb
 */
package org.sonarsource.sonarlint.core.commons.storage;

import io.sentry.Sentry;
import io.sentry.ScopeCallback;
import io.sentry.logger.ILoggerApi;
import java.sql.SQLException;
import org.jooq.ExecuteContext;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

class JooqDatabaseExceptionListenerTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private JooqDatabaseExceptionListener listener;
  private MockedStatic<Sentry> sentryMock;

  @BeforeEach
  void setUp() {
    listener = new JooqDatabaseExceptionListener();
    DatabaseExceptionReporter.clearRecentExceptions();
    sentryMock = mockStatic(Sentry.class);
    sentryMock.when(Sentry::logger).thenReturn(mock(ILoggerApi.class));
  }

  @AfterEach
  void tearDown() {
    sentryMock.close();
    DatabaseExceptionReporter.clearRecentExceptions();
  }

  @Test
  void should_report_sql_exception_from_context() {
    var ctx = mock(ExecuteContext.class);
    var sqlException = new SQLException("SQL syntax error", "42000", 1064);
    var runtimeException = new RuntimeException("Wrapped exception", sqlException);

    when(ctx.exception()).thenReturn(runtimeException);
    when(ctx.sqlException()).thenReturn(sqlException);
    when(ctx.sql()).thenReturn("SELECT * FROM invalid_table");

    listener.exception(ctx);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));
    assertThat(exceptionCaptor.getValue()).isInstanceOf(SQLException.class);
    assertThat(((SQLException) exceptionCaptor.getValue()).getSQLState()).isEqualTo("42000");
  }

  @Test
  void should_report_runtime_exception_when_no_sql_exception() {
    var ctx = mock(ExecuteContext.class);
    var runtimeException = new RuntimeException("jOOQ execution failed");

    when(ctx.exception()).thenReturn(runtimeException);
    when(ctx.sqlException()).thenReturn(null);
    when(ctx.sql()).thenReturn("INSERT INTO test VALUES (1)");

    listener.exception(ctx);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));
    assertThat(exceptionCaptor.getValue()).isSameAs(runtimeException);
  }

  @Test
  void should_not_report_when_no_exception() {
    var ctx = mock(ExecuteContext.class);

    when(ctx.exception()).thenReturn(null);

    listener.exception(ctx);

    sentryMock.verify(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)), never());
  }

  @Test
  void should_handle_null_sql() {
    var ctx = mock(ExecuteContext.class);
    var exception = new RuntimeException("Error without SQL");

    when(ctx.exception()).thenReturn(exception);
    when(ctx.sqlException()).thenReturn(null);
    when(ctx.sql()).thenReturn(null);

    listener.exception(ctx);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));
    assertThat(exceptionCaptor.getValue()).isSameAs(exception);
  }

  @Test
  void should_prefer_sql_exception_over_runtime_exception() {
    var ctx = mock(ExecuteContext.class);
    var sqlException = new SQLException("Constraint violation", "23000", 1062);
    var runtimeException = new RuntimeException("Wrapper", sqlException);

    when(ctx.exception()).thenReturn(runtimeException);
    when(ctx.sqlException()).thenReturn(sqlException);
    when(ctx.sql()).thenReturn("INSERT INTO test (id) VALUES (1)");

    listener.exception(ctx);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));
    assertThat(exceptionCaptor.getValue()).isSameAs(sqlException);
  }
}
