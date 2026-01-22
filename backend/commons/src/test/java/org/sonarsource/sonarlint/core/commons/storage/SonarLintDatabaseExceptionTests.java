/*
ACR-320734d0f424499583d3e5d212562fb6
ACR-42e60eb6cf3644f29104ccaf83c1c921
ACR-c6502df39ac747838880486ac49ae2e9
ACR-710e4f32d3504bb6bfdab833517edd45
ACR-9caf5f62970142ab9ac1765de1917536
ACR-f86bcd2b100d4ae496b8e1c2bf771306
ACR-ffcd4a232b544af3964762c9d2d83321
ACR-780621a5e0864be5894345985e13464e
ACR-31003bfe93d04891906c538bd0797c4e
ACR-78ad40d048774871832a973b23bfce1c
ACR-f2111f7029c544079bcc4745c5ffa909
ACR-82b1b5a3d41a40348ae7bef2d5a01ded
ACR-287429f770324a3db8d48adeeb547122
ACR-402ec0078184429c86368adca8372a17
ACR-a01487bfaaca483f8006b65b745f85de
ACR-503b1cae84d0427da33b2dc4b30210cc
ACR-56516ff70c0e44cf9e0dfb360a96de45
 */
package org.sonarsource.sonarlint.core.commons.storage;

import io.sentry.Sentry;
import io.sentry.ScopeCallback;
import io.sentry.logger.ILoggerApi;
import java.nio.file.Path;
import java.sql.SQLException;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

class SonarLintDatabaseExceptionTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private SonarLintDatabase db;
  private MockedStatic<Sentry> sentryMock;

  @BeforeEach
  void setUp() {
    DatabaseExceptionReporter.clearRecentExceptions();
    sentryMock = mockStatic(Sentry.class);
    sentryMock.when(Sentry::logger).thenReturn(mock(ILoggerApi.class));
  }

  @AfterEach
  void tearDown() {
    if (db != null) {
      db.shutdown();
    }
    sentryMock.close();
    DatabaseExceptionReporter.clearRecentExceptions();
  }

  @Test
  void should_report_runtime_sql_exception_via_listener(@TempDir Path tempDir) {
    var storageRoot = tempDir.resolve("storage");
    db = new SonarLintDatabase(storageRoot);

    assertThatThrownBy(() -> db.dsl().execute("SELECT * FROM non_existent_table"))
      .isInstanceOf(DataAccessException.class);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));

    var capturedException = exceptionCaptor.getValue();
    assertThat(capturedException).isInstanceOf(SQLException.class);
    var sqlException = (SQLException) capturedException;
    assertThat(sqlException.getSQLState()).isEqualTo("42S02");
    assertThat(sqlException.getMessage()).contains("NON_EXISTENT_TABLE");
  }

  @Test
  void should_report_invalid_sql_syntax_exception(@TempDir Path tempDir) {
    var storageRoot = tempDir.resolve("storage");
    db = new SonarLintDatabase(storageRoot);

    assertThatThrownBy(() -> db.dsl().execute("INVALID SQL SYNTAX HERE"))
      .isInstanceOf(DataAccessException.class);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));

    var capturedException = exceptionCaptor.getValue();
    assertThat(capturedException).isInstanceOf(SQLException.class);
    var sqlException = (SQLException) capturedException;
    assertThat(sqlException.getSQLState()).isEqualTo("42001");
  }

  @Test
  void should_report_constraint_violation_exception(@TempDir Path tempDir) {
    var storageRoot = tempDir.resolve("storage");
    db = new SonarLintDatabase(storageRoot);

    db.dsl().execute("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, name VARCHAR(100))");
    db.dsl().execute("INSERT INTO test_table (id, name) VALUES (1, 'test')");

    assertThatThrownBy(() -> db.dsl().execute("INSERT INTO test_table (id, name) VALUES (1, 'duplicate')"))
      .isInstanceOf(DataAccessException.class);

    var exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
    sentryMock.verify(() -> Sentry.captureException(exceptionCaptor.capture(), any(ScopeCallback.class)));

    var capturedException = exceptionCaptor.getValue();
    assertThat(capturedException).isInstanceOf(SQLException.class);
    var sqlException = (SQLException) capturedException;
    assertThat(sqlException.getSQLState()).isEqualTo("23505");
    assertThat(sqlException.getMessage()).contains("Unique index or primary key violation");
  }

  @Test
  void should_initialize_database_successfully(@TempDir Path tempDir) {
    var storageRoot = tempDir.resolve("storage");
    db = new SonarLintDatabase(storageRoot);

    assertThat(db.dsl()).isNotNull();
    sentryMock.verify(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)), never());
  }

  @Test
  void should_shutdown_database_successfully(@TempDir Path tempDir) {
    var storageRoot = tempDir.resolve("storage");
    db = new SonarLintDatabase(storageRoot);

    db.shutdown();
    db = null;

    sentryMock.verify(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)), never());
  }

  @Test
  void should_execute_valid_queries_without_exception_reporting(@TempDir Path tempDir) {
    var storageRoot = tempDir.resolve("storage");
    db = new SonarLintDatabase(storageRoot);

    db.dsl().execute("CREATE TABLE IF NOT EXISTS valid_table (id INT, name VARCHAR(100))");
    db.dsl().execute("INSERT INTO valid_table (id, name) VALUES (1, 'test')");
    var result = db.dsl().fetch("SELECT * FROM valid_table WHERE id = 1");

    assertThat(result).hasSize(1);
    sentryMock.verify(() -> Sentry.captureException(any(Throwable.class), any(ScopeCallback.class)), never());
  }

}
