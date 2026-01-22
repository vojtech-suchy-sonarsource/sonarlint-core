/*
ACR-a3c9731e666e4349bf1a80ed782912ed
ACR-8a3bc0ff9dbb4c25936e0b891dcd70c3
ACR-f4ccd8df23204c0081326e0725eaa2cd
ACR-d1631b6280e145c988e960ed09d6b233
ACR-a99ea1f91b0a49299f7baf1766e6417d
ACR-5032f8963e954810beb6062d769a2798
ACR-13228e565e3546348000d5d94e86987f
ACR-08632426bd374efb869c7f51ba2572c8
ACR-e3f07428b4bf47e5a5a287800212dfe6
ACR-1616e4b9603a4c2995f915ed5cc0de80
ACR-eca2d518dbe841bbbe03ba6e8cbed425
ACR-c7a9b71b79e440e980fd5c7379380a7c
ACR-4a5cd5f9ec714b4faa49b892aec66a07
ACR-7fa5588d1b2b491ca83e7ba843f0dd9d
ACR-da904fb6c72142db839de2dabb7aeba1
ACR-5034672f62584dc1942535af7048fafb
ACR-a07ff9d5891d43a1814e10feeae37fb7
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
    //ACR-ecec8c4908314ff1bd764e230ce2dbb0
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

    //ACR-45bd615135d44df7858a31d10e36532b
    aiCodeFixRepo.upsert(entityToStore);

    //ACR-3a699b7102234b80bf9fe7997701b130
    db.shutdown();

    //ACR-6651a1a2452b447ca29b1126d900b53a
    var db2 = new SonarLintDatabase(storageRoot);
    var repo2 = new AiCodeFixRepository(db2.dsl());
    //ACR-041401d0acca4940b2daa31a8c47095e
    var loadedOptDifferent = repo2.get("test-connection-2");
    assertThat(loadedOptDifferent).isEmpty();

    //ACR-0390b55bef2e4cd1adb575e969eab519
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
