/*
ACR-fa498f38abaa4f5486ebc661885135bb
ACR-767186437eb44a92947f9a2504b9260e
ACR-7322c03719e34116a68b5d8d9a967baa
ACR-50cfaf7833c34e0987f3faefa1d6f45f
ACR-4d73a44835ed42cb922c6613c9770d52
ACR-fda4f5271a9b40dd803ada79e470ccb3
ACR-b6da640f6a79416a808f30403efc4687
ACR-a59e249861334e5ba49d21fd18a64c44
ACR-441b439701ae420ca6fda7bea0b73e4f
ACR-4cf594247b57484eb94e6780f88f56d0
ACR-e1a9ab76ed0c4551bd54716632c61898
ACR-badafec1d18b42c0b231ad1c009c0ea0
ACR-ba0b511fa0d143a197d495036d0ad59f
ACR-619dbac6beb04009baed0a17579a8e95
ACR-4b40637b77e541bc8bd1201050ce2495
ACR-718bfd4b5c6445deb148dd07f75a7a0b
ACR-3aa849e2f69647d4870097ae00474d84
 */
package mediumtest.hotspots;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckStatusChangePermittedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckStatusChangePermittedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;

class HotspotCheckStatusChangePermittedMediumTests {

  @SonarLintTest
  void it_should_fail_when_the_connection_is_unknown(SonarLintTestHarness harness) {
    var backend = harness.newBackend().start();

    var response = checkStatusChangePermitted(backend, "connectionId");

    assertThat(response)
      .failsWithin(Duration.ofSeconds(2))
      .withThrowableOfType(ExecutionException.class)
      .havingCause()
      .isInstanceOf(ResponseErrorException.class)
      .withMessage("Connection 'connectionId' is gone");
  }

  @SonarLintTest
  void it_should_return_3_statuses_for_sonarcloud(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarCloudServer()
      .withOrganization("orgKey", organization -> organization
        .withProject("projectKey", project -> project
          .withDefaultBranch(branch -> branch.withHotspot("hotspotKey"))))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .withSonarCloudConnection("connectionId", "orgKey")
      .start();

    var response = checkStatusChangePermitted(backend, "connectionId");

    assertThat(response)
      .succeedsWithin(Duration.ofSeconds(2))
      .extracting(CheckStatusChangePermittedResponse::isPermitted, CheckStatusChangePermittedResponse::getNotPermittedReason,
        CheckStatusChangePermittedResponse::getAllowedStatuses)
      .containsExactly(true, null, List.of(HotspotStatus.TO_REVIEW, HotspotStatus.FIXED, HotspotStatus.SAFE));
  }

  @SonarLintTest
  void it_should_return_4_statuses_for_sonarqube(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().withProject("projectKey", project -> project.withDefaultBranch(branch -> branch.withHotspot("hotspotKey"))).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .start();

    var response = checkStatusChangePermitted(backend, "connectionId");

    assertThat(response)
      .succeedsWithin(Duration.ofSeconds(2))
      .extracting(CheckStatusChangePermittedResponse::isPermitted, CheckStatusChangePermittedResponse::getNotPermittedReason,
        CheckStatusChangePermittedResponse::getAllowedStatuses)
      .containsExactly(true, null, List.of(HotspotStatus.TO_REVIEW, HotspotStatus.ACKNOWLEDGED, HotspotStatus.FIXED, HotspotStatus.SAFE));
  }

  @SonarLintTest
  void it_should_not_be_changeable_when_permission_missing(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey",
        project -> project.withDefaultBranch(branch -> branch.withHotspot("hotspotKey",
          hotspot -> hotspot.withoutStatusChangePermission())))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .start();

    var response = checkStatusChangePermitted(backend, "connectionId");

    assertThat(response)
      .succeedsWithin(Duration.ofSeconds(2))
      .extracting(CheckStatusChangePermittedResponse::isPermitted, CheckStatusChangePermittedResponse::getNotPermittedReason,
        CheckStatusChangePermittedResponse::getAllowedStatuses)
      .containsExactly(false, "Changing a hotspot's status requires the 'Administer Security Hotspot' permission.",
        List.of(HotspotStatus.TO_REVIEW, HotspotStatus.ACKNOWLEDGED, HotspotStatus.FIXED, HotspotStatus.SAFE));
  }

  private CompletableFuture<CheckStatusChangePermittedResponse> checkStatusChangePermitted(SonarLintTestRpcServer backend, String connectionId) {
    return backend.getHotspotService().checkStatusChangePermitted(new CheckStatusChangePermittedParams(connectionId, "hotspotKey"));
  }
}
