/*
ACR-77cc4e86f146475d90f9f70a0a2276a7
ACR-654b641a385d4dda9ea61a4a397b697f
ACR-0ba88ce95c6748b585db740ad7054b45
ACR-c52353bb3b8f4db68023c39978ce0330
ACR-cbb63499dbb14382b9772ff50f99aee4
ACR-79e552964a1048a48d2cbd036e163704
ACR-db6c983eb4db4b4e8ddde736df9912dd
ACR-4779f20bcab543c6885bae4904f1f4df
ACR-75597c37299840aea22d85d3644c17d3
ACR-4551667899874cae89f5bbd3bbdd7dd2
ACR-5787f227ad1745bc8a46331ad904db3a
ACR-45c6f7cfc0ae435d8acbda273999ef4b
ACR-b984db6d69c34dbe8e3951815d396a20
ACR-7878d642150c4dfe97b1c66a4be3e771
ACR-b898b353ea1e47a9b49a19d8cff5f1f1
ACR-c93cc419797b49c49da3814a6f6657ad
ACR-11581196785c44ebb4a5cd212f61f0c3
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
      //ACR-eff2308866904c1f884ab8a2fb588caf
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
      //ACR-44aa589a884f4ad7addf4e06fcc82689
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
    //ACR-1338609770a4444aa32cf9b4ce68fb39
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
