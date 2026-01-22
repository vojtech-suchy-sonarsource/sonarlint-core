/*
ACR-eae96a121fdf47988ba3e416831f73d9
ACR-d2af8b6cad984043814f4c6826bfa548
ACR-48a00a5f92c54e1ea906a1fa78f4a579
ACR-e11e305897a34525b609ca1e0931864b
ACR-24743e9718644c0e9111b56b1e97369e
ACR-8d4dd1050e7a4a8fabb4b3c161338b8f
ACR-f039552ef0bc4ea2956a32e12a3e5bec
ACR-0a59a968cda64f729d10282329cb13de
ACR-82256d0da39349c9b3d138211c4a0158
ACR-ec943f38bc084905ad59e74b4a38d04e
ACR-a9a448da487a4e1f9ea3bd5e0dd4923a
ACR-61d5f6c64c3342aaa43d2dbe8b496349
ACR-b66f86fd2ee14403aba792c91b49b201
ACR-1fc610f9e42b4355916e418a4f010202
ACR-c6d6ef4d146d4c68b16f19df939b52af
ACR-75119598d01a43099386c6ac3b632832
ACR-b012a47782a448aa9300df75d12bd92e
 */
package org.sonarsource.sonarlint.core.serverconnection.aicodefix;

import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;

import static org.assertj.core.api.Assertions.assertThat;

class AiCodeFixRepositoryTest {

  @RegisterExtension
  static SonarLintLogTester logTester = new SonarLintLogTester();

  @TempDir
  Path temp;

  @Test
  void upsert_and_get_should_persist_to_h2_file_database() {
    //ACR-8753e15773d647788b6b093dc3702c9b
    var storageRoot = temp.resolve("storage");

    var db = new SonarLintDatabase(storageRoot);
    var aiCodeFixRepo = new AiCodeFixRepository(db.dsl());

    var entityToStore = new AiCodeFix(
      "test-connection",
      Set.of("java:S100", "js:S200"),
      true,
      AiCodeFix.Enablement.ENABLED_FOR_SOME_PROJECTS,
      Set.of("project-a", "project-b")
    );

    //ACR-97c75cdf421d40108143ed41d8280acb
    aiCodeFixRepo.upsert(entityToStore);

    //ACR-ac8ded77586641dca3b4e75aede6bff9
    db.shutdown();

    //ACR-8d7708fa36664c8d9faceeba93a180dd
    var db2 = new SonarLintDatabase(storageRoot);
    var repo2 = new AiCodeFixRepository(db2.dsl());
    //ACR-03ae9c3991484a00bbf1fdb88d00b62c
    var loadedOptDifferent = repo2.get("test-connection-2");
    assertThat(loadedOptDifferent).isEmpty();

    //ACR-e0f686060522429382d61a7a150d0c12
    var repoSame = new AiCodeFixRepository(db2.dsl());
    var loadedOpt = repoSame.get("test-connection");
    assertThat(loadedOpt).isPresent();
    var loaded = loadedOpt.get();

    assertThat(loaded.supportedRules()).containsExactlyInAnyOrder("java:S100", "js:S200");
    assertThat(loaded.organizationEligible()).isTrue();
    assertThat(loaded.enablement()).isEqualTo(AiCodeFix.Enablement.ENABLED_FOR_SOME_PROJECTS);
    assertThat(loaded.enabledProjectKeys()).containsExactlyInAnyOrder("project-a", "project-b");

    db2.shutdown();
  }
}
