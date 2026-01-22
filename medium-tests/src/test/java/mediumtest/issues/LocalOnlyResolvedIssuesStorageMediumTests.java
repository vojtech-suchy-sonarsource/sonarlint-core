/*
ACR-f13a053b0e7b4ed6aac1d2ef61e465c0
ACR-ca7a115624eb49a0aee0753766d2cfbf
ACR-ccb2c093e47c4e698de6c789501d880b
ACR-30ff58ad14b44db7b51c2a3530e4e5e7
ACR-c9db509d4f4d4c6b8aa2feb851498c78
ACR-02938bb0c5424cfabeb4558a1c1f0f3b
ACR-bdadd0ae6bc144bba584a6f3d19acb81
ACR-f47f8a314bc54c1a96f571462fdaa3ab
ACR-a9a5209a6f1d492b8bfaeec2cfe3edc9
ACR-51e7f2c625344d1f8180505b798a54ea
ACR-d0664e18ac3647ceb825690563e94c30
ACR-89f484057eb141f7909462f9c45f40e6
ACR-92450ae1c07f4e51bda813cd0a07f155
ACR-8adc8c02a97e4e1d9f8473fb9cca987b
ACR-64839b2a0c3844ecb387ad39864ca53b
ACR-ba979c552d6049718a56bfd25365c391
ACR-f7d10fa1efae4ce796366dfa4e2d3314
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
