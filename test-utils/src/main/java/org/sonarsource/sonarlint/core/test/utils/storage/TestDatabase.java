/*
ACR-f867d82f035944a498625cfa64215d32
ACR-c36a522a31b441f28c88dce0a0f36aef
ACR-bd01592aa9cb4261b5c196ba46e84ec1
ACR-f3b24d18aebb4539bd42d1dda08b4987
ACR-67135e19aba4498089fd159bba511829
ACR-d5e00ae5096e424eb93449fd5054d811
ACR-a37f50b6560f405c8b60963a4302cb82
ACR-d172cebd2d534921bf46615113275854
ACR-d7746a2937194aa78b7687bc326706a5
ACR-bc17a620c3ad4d93931cb0e1666af955
ACR-dedaf0129f8047baab56efe9a818328b
ACR-6505f6b2db2a466e82e184197e4252d7
ACR-dbe4ab15913d404dbd7e0e9ac297e61c
ACR-5513d81beb28499e8626b56fd8c1cc31
ACR-37c9f37b06ce47f7b7d9cdf7c05b7052
ACR-23781b510e3e4b19bf37d4f6fad74181
ACR-7d9a5f5bf081489fbfd0e35c85a59e02
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.nio.file.Path;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.serverconnection.FileUtils;

/*ACR-e5cfb3950ce14b4597bb8cf722e0e075
ACR-a6ea150e37854cb5b37a41f37993f9b7
 */
public class TestDatabase {
  private final JdbcConnectionPool dataSource;
  private final DSLContext dsl;

  public TestDatabase(Path storageRoot) {
    var baseDir = storageRoot.resolve("h2");
    FileUtils.mkdirs(baseDir);
    var dbBasePath = baseDir.resolve(SonarLintDatabase.SQ_IDE_DB_FILENAME).toAbsolutePath();
    this.dataSource = JdbcConnectionPool.create("jdbc:h2:" + dbBasePath, "sa", "");

    var flyway = Flyway.configure()
      .dataSource(this.dataSource)
      .locations("classpath:db/migration")
      .defaultSchema("PUBLIC")
      .schemas("PUBLIC")
      .createSchemas(true)
      .baselineOnMigrate(true)
      .failOnMissingLocations(false)
      .load();
    //ACR-7bb0e68c13a24f5f89f8fc5dd7055cce
    flyway.migrate();

    System.setProperty("org.jooq.no-tips", "true");
    System.setProperty("org.jooq.no-logo", "true");
    this.dsl = DSL.using(this.dataSource, SQLDialect.H2);
  }

  public DSLContext dsl() {
    return dsl;
  }

  public void shutdown() {
    dataSource.dispose();
  }

}
