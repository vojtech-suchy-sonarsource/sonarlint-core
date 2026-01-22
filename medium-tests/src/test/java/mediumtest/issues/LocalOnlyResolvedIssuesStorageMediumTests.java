/*
ACR-72f24ab1b4a84b47bc45756f1ef76f81
ACR-d1b9aa2fb30347babe0c9c38ad61e0fa
ACR-8293048cde9c4171a145ab348794273e
ACR-6e89bf8b09834cd2be3aaee91490a4a6
ACR-15ebd0580c9848b781d5fdff61e5ff07
ACR-0d6f1ace53b3433599a3590bc1aef663
ACR-b842495772744b5699e1dec930cdd101
ACR-79c6a229b0e14326bb6048ccad2ca3f6
ACR-f93cd5489f8f4ff8bddcb32e5dd974fa
ACR-a2c048a4cd55472e9926f2bf873c459a
ACR-af7c9df5d454470a98fb876a9c753383
ACR-fc1f3dfd85074a58b2d9c68d2a749d70
ACR-b11278bf6bc44b90b9f39f7b49716110
ACR-df045fe3fc6242f9962565fa92dea795
ACR-6df673c5655444899e748799e65acd44
ACR-787fd8ef187343c4ac9834e77c3345ac
ACR-2bc9c726f57048a6b844ffcb06fe2a9b
 */
package mediumtest.issues;

import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static mediumtest.fixtures.LocalOnlyIssueFixtures.aLocalOnlyIssueResolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.commons.dogfood.DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY;
import static org.sonarsource.sonarlint.core.test.utils.storage.ServerIssueFixtures.aServerIssue;

@ExtendWith(SystemStubsExtension.class)
class LocalOnlyResolvedIssuesStorageMediumTests {

  @SystemStub
  EnvironmentVariables environmentVariables;

  @BeforeEach
  void prepare() {
    environmentVariables.remove(SONARSOURCE_DOGFOODING_ENV_VAR_KEY);
  }

  @SonarLintTest
  void it_should_purge_local_only_stored_issues_resolved_more_than_one_week_ago_at_startup(SonarLintTestHarness harness) {
    var serverIssue = aServerIssue("myIssueKey").withTextRange(new TextRangeWithHash(1, 2, 3, 4, "hash")).withIntroductionDate(Instant.EPOCH.plusSeconds(1)).withType(RuleType.BUG);
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server.baseUrl(), storage -> storage
        .withProject("projectKey", project -> project.withMainBranch("main", branch -> branch.withIssue(serverIssue)))
        .withServerVersion("9.8"))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey",
        storage -> storage.withLocalOnlyIssue(aLocalOnlyIssueResolved(Instant.now().minus(1, ChronoUnit.MINUTES).minus(7, ChronoUnit.DAYS))))
      .start();

    var storedIssues = backend.getLocalOnlyIssuesRepository().loadAll("configScopeId");

    assertThat(storedIssues).isEmpty();
  }

  @SonarLintTest
  void it_should_migrate_the_local_only_issues_from_xodus_to_the_new_h2_database(SonarLintTestHarness harness) {
    environmentVariables.set(SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "1");
    var serverIssue = aServerIssue("myIssueKey").withTextRange(new TextRangeWithHash(1, 2, 3, 4, "hash")).withIntroductionDate(Instant.EPOCH.plusSeconds(1)).withType(RuleType.BUG);
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", harness.newFakeSonarQubeServer().start(), storage -> storage
        .withProject("projectKey", project -> project.withMainBranch("main", branch -> branch.withIssue(serverIssue)))
        .withServerVersion("9.8"))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey",
        storage -> storage
          .usingXodus()
          .withLocalOnlyIssue(aLocalOnlyIssueResolved()))
      .start();

    var issues = backend.getLocalOnlyIssuesRepository().loadForFile("configScopeId", Paths.get("file/path"));

    assertThat(issues).hasSize(1);
  }
}
