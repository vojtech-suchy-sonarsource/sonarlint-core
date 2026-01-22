/*
ACR-fea17f9686b947bab85bb60c6168a559
ACR-a15eb739f1a647469688d80464dc31c4
ACR-8c5a0783d9224d31b0513fb8a16965f8
ACR-646dcf125034458b9ce145db1b441946
ACR-e7f69b28db4a4d439c278775558a91c5
ACR-09a6a5c9759542658180775ec6e427f3
ACR-112c835c083240cfb1def5ab50a92ee8
ACR-b4cf442bd25245df9fda60ee24e58173
ACR-d5ecf79661c84ce49e683007bdeb8413
ACR-426378a2a2cc44e58f7301e01ce453bf
ACR-dccd9d16460644ba97bcaeacded05785
ACR-72fc5ff4da1445c5a3640e0f118b80dd
ACR-fc43245663dc41eba421878969728d60
ACR-01e18a9ca88944e4affdae87824b7360
ACR-df8b026c2cec4d578811f51d41ec9060
ACR-0a469eee25b14f7895fbf087bbb8ddeb
ACR-1fdcafccbd9841a18b1bb3aaa73b7915
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
