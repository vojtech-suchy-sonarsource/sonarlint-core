/*
ACR-28542f6d141c4cdb88cbfff90bc1051c
ACR-aa8f97ae72f5472883ec8bd07c53a4f1
ACR-39db0a8d316341e1af36110c39b290af
ACR-82468bf2f48e4198a0fe8bf876f499ff
ACR-843dbdd8669e40cbb5bbbcf6d8126376
ACR-d03d0fe978e9477d9c53ccfeb9390236
ACR-22d46713ccab41ad9058762014adbcd8
ACR-0da059c9cd304588bf65016af8afd789
ACR-7ea218a661724e12902d9059642e883a
ACR-8025fdf57cb046718548d436dac3159f
ACR-6764b222dc3947f7bad7530d04469b6b
ACR-f887ed3707764216ad97df86fcbeb93e
ACR-1e33896aaa1141beac9e99c47f3f2852
ACR-40e9bd1f608e469c802ce62b2191d373
ACR-c7311c95a7124315bacce05923c081c0
ACR-e3b210d7aa3346ea8fab17dc0fe8d52a
ACR-ebc1fa8d02db42389e5d72326a483276
 */
package mediumtest.hotspots;

import java.net.MalformedURLException;
import java.net.URL;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.OpenHotspotInBrowserParams;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class OpenHotspotInBrowserMediumTests {

  @SonarLintTest
  void it_should_open_hotspot_in_sonarqube(SonarLintTestHarness harness) throws MalformedURLException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", "http://localhost:12345", storage -> storage.withProject("projectKey", project -> project.withMainBranch("master")))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withTelemetryEnabled()
      .start(fakeClient);

    backend.getHotspotService().openHotspotInBrowser(new OpenHotspotInBrowserParams("scopeId", "ab12ef45"));

    verify(fakeClient, timeout(5000)).openUrlInBrowser(new URL("http://localhost:12345/security_hotspots?id=projectKey&branch=master&hotspots=ab12ef45"));

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent().openHotspotInBrowserCount()).isEqualTo(1));
  }

  @SonarLintTest
  void it_should_not_open_hotspot_if_unbound(SonarLintTestHarness harness) throws InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withUnboundConfigScope("scopeId")
      .start(fakeClient);

    backend.getHotspotService().openHotspotInBrowser(new OpenHotspotInBrowserParams("scopeId", "ab12ef45"));

    Thread.sleep(100);

    verify(fakeClient, never()).openUrlInBrowser(any());
  }

}
