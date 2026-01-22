/*
ACR-daf500f243d44d3bb16cc3a823b17ca4
ACR-8c29bc8b1a4740608c02f29b819b00a5
ACR-d9d0df16e3d34b178be32496914f1672
ACR-481cff2607d24c8b9b68674e7b2e884f
ACR-69736e0864e94d9280d33f126560cf95
ACR-62325082e19f4f58aa0fa09946f29f37
ACR-7cd549832a6542b5968a69bff6392773
ACR-5e48f5dc28004804a5e57d13876d8e5f
ACR-c74002ec6322479d939f5d49800cd3cb
ACR-ac4aebbca6ba4913b184a05ea50a8cf0
ACR-9f4cadc6a5264c0eb416bd7b2a13f70c
ACR-dce095118c804280b3af6f2a81b08b90
ACR-f80c8b87295c4215a1fd271d9ace1a4d
ACR-56b89820f810404789364e09eca20f96
ACR-5fc5a8b978c24e3d906a68662dcedd88
ACR-d893647c50d04f98b6c9ffbb70536f1f
ACR-498abc47ecf942e7840984825bb2fd95
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
