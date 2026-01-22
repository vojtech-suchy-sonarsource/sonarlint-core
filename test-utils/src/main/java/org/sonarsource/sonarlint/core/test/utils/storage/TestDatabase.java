/*
ACR-b5ffaf7c7f2447e685e17b8b9a5e55f9
ACR-b3ed680511ee4cf4b0a775b63d5adb43
ACR-581275cb4b5d4836927e6615d74ce3c1
ACR-de9d9bab975a480aab3207510121cbdb
ACR-1ac58b13ced646fa98a89c1660faf63a
ACR-6978b43dfe6f4e9f84d4360088ba0fbd
ACR-23dc9456081b45a7816784ed53177628
ACR-b9f4360505804b099159b6ab37317c41
ACR-dad319fcfd544a20b651c079cffc0858
ACR-589fb7eed81f44f5a42b57671602c182
ACR-bafc44f2869a437eb0eee3fa78ec8e42
ACR-fea1916e8489454fac493c02489db09e
ACR-6cd554058a114bd8b8d3f927617b7bfd
ACR-95ef15a0a7194a74b233018a54a024c2
ACR-4031a523456a40eb91d40d1778613f72
ACR-25f1807e65804f60913082a31f93c450
ACR-a5c2e215988547ca954aad913065d514
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

/*ACR-6bdfd4bbe1c340a194e10b92428aedf0
ACR-f911d019738249cb8110eec5eeae5469
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
    //ACR-3c2efe6d3b5f48d5a58cb0c509a47e56
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
