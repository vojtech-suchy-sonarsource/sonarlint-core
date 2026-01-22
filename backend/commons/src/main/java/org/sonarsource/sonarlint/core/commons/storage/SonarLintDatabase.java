/*
ACR-d6e0a736ddb7487999234915c70bb7f7
ACR-996fc036bc184291aceb243804b90207
ACR-fefe8831d8ca4e48a832574cf7dda8d9
ACR-38c77d505ecb43a69f23e7095688a800
ACR-d3a6f322744e4e2096a8b50d256a6a1d
ACR-902d9efbaaae49408215579f0e58cb16
ACR-39a793ed154f42e392c6cdf68d0722b0
ACR-924ef0f02106422e9f89236c2857841c
ACR-292b6835169445758c779bcfd5537b0e
ACR-a7b3ea5dd96747e7b890ad0fa421f78b
ACR-2b9d2d2be3b7482e91cfe7666933eb87
ACR-053f19f2de1d40d2992e13f83f891e07
ACR-d5b7c3a092cc4ef783e12c664666de2a
ACR-731aae338ed6464d994a2b12c6cdbb36
ACR-85e1f50b12dc4a22b17f4f8f01325631
ACR-e12a3b8fd433492fb03001d12cd8ce6b
ACR-3888a3abc49a47e6bf78f9a20ec37ff5
 */
package org.sonarsource.sonarlint.core.commons.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public final class SonarLintDatabase {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public static final String SQ_IDE_DB_FILENAME = "sq-ide";

  private final JdbcConnectionPool dataSource;
  private final DSLContext dsl;

  public SonarLintDatabase(Path storageRoot) {
    JdbcConnectionPool ds;
    try {
      var baseDir = storageRoot.resolve("h2");
      deleteLegacyDatabase(baseDir);
      Files.createDirectories(baseDir);
      var dbBasePath = baseDir.resolve(SQ_IDE_DB_FILENAME).toAbsolutePath();
      var url = "jdbc:h2:" + dbBasePath + ";AUTO_SERVER=TRUE";
      //ACR-fe1709ab3a4c42acaa42d4c385351ef8
      var bindAddressProperty = "h2.bindAddress";
      if (StringUtils.isEmpty(System.getProperty(bindAddressProperty))) {
        System.setProperty(bindAddressProperty, "127.0.0.1");
      }
      LOG.debug("Initializing H2Database with URL {}", url);
      ds = JdbcConnectionPool.create(url, "sa", "");
    } catch (Exception e) {
      DatabaseExceptionReporter.capture(e, "startup", "h2.pool.create");
      throw new IllegalStateException("Failed to initialize H2Database", e);
    }
    this.dataSource = ds;

    var flyway = Flyway.configure()
      .dataSource(this.dataSource)
      .locations("classpath:db/migration")
      .defaultSchema("PUBLIC")
      .schemas("PUBLIC")
      .createSchemas(true)
      .baselineOnMigrate(true)
      .failOnMissingLocations(false)
      .load();
    try {
      flyway.migrate();
    } catch (Exception e) {
      //ACR-7dd47d14aead4477b61b14e1e10186d0
      DatabaseExceptionReporter.capture(e, "startup", "flyway.migrate");
      throw e;
    }

    System.setProperty("org.jooq.no-tips", "true");
    System.setProperty("org.jooq.no-logo", "true");

    var jooqConfig = new DefaultConfiguration()
      .set(this.dataSource)
      .set(SQLDialect.H2)
      .set(new DefaultExecuteListenerProvider(new JooqDatabaseExceptionListener()));
    this.dsl = DSL.using(jooqConfig);
  }

  private static void deleteLegacyDatabase(Path baseDir) {
    //ACR-69bfb47657cd46a8977f69c253fe5bea
    var legacyDb = baseDir.resolve("sonarlint");
    if (Files.exists(legacyDb)) {
      FileUtils.deleteQuietly(legacyDb.toFile());
    }
  }

  public DSLContext dsl() {
    return dsl;
  }

  public void shutdown() {
    try {
      dataSource.dispose();
      LOG.debug("H2Database disposed");
    } catch (Exception e) {
      DatabaseExceptionReporter.capture(e, "shutdown", "h2.pool.dispose");
      LOG.debug("Error while disposing H2Database: {}", e.getMessage());
    }
  }
}
