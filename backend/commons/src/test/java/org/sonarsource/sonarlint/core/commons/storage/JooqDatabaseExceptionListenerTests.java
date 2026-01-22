/*
ACR-f0b4694cb67e497ca3057b2de885b2dd
ACR-183cfedd1f1f4c1c80f45fbfa2a6275b
ACR-359b5de791ae49ac84a6fdca92c2072b
ACR-03c2c767082742a1b6a73e278851d516
ACR-9df621f3c4494a0b9326b2fb7e94f97d
ACR-2c53e8f395054c058e880c7ba37456eb
ACR-63f6f3eb5f104604b593aecb178e251b
ACR-15e15a9715b144f98746862351528d97
ACR-485e71bc014c462fb5837dc765a23873
ACR-87205d0b80b348baa35fe7913ac3ac79
ACR-02b2f59befd94940a56aec4bf21b55ae
ACR-fc8fb8cb4860449ba54cb3021bfb0057
ACR-7661d0bef04043519396d192573842f9
ACR-5b55ee9c09804ea6ab9f3751d9fda45a
ACR-87e4311b6f384edb8ac9a8f386c39a7b
ACR-b0b368c048a94e2e9b1df7383d7f9f2f
ACR-ab4579967a8e40b9a1613b8c2dcf1d20
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
