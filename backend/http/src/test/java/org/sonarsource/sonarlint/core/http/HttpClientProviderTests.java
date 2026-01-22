/*
ACR-7711d2a351f840ceb3a02a19a84515dc
ACR-55856416cd0245f699f4bcc7407a59e8
ACR-430b83b3ed2742c19e18de4146d2e2da
ACR-9ef79d0fbbff48ad91791e1d65217bd3
ACR-6a7f9cbdbb274fbc8dc097ce1d25b714
ACR-ed338d8031464828902ee60638ceeaa1
ACR-51c40f1aaa1f40eb97d38c820fc6f49b
ACR-f9605ecc09854fd3a09ec413ebbdb4b6
ACR-b183f54c182542a6b429e84073f5b824
ACR-ead35031350646e590dbb91880db6f80
ACR-23fad8adcd844c9a8784d672f73e1f45
ACR-fffc2f68cc7441d38fdebea1243adf92
ACR-20f8840a1f014fd7ab3b13f3da842c3e
ACR-e70a2e3a2e4c4039ba4945ae45069080
ACR-7286104b1ca340c7840be9965048bc5f
ACR-276ce00811ea4091ac7d11af77ba125a
ACR-c4188e3b96944b4b84ef41a6dbd1ec0f
 */
package org.sonarsource.sonarlint.core.http;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpClientProviderTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static WireMockExtension sonarqubeMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @Test
  void it_should_use_user_agent() {
    var underTest = HttpClientProvider.forTesting();

    underTest.getHttpClient().get(sonarqubeMock.url("/test"));

    sonarqubeMock.verify(getRequestedFor(urlEqualTo("/test"))
      .withHeader("User-Agent", equalTo("SonarLint tests")));
  }

  @Test
  void it_should_support_cancellation() {
    sonarqubeMock.stubFor(get("/delayed")
      .willReturn(aResponse()
        .withFixedDelay(20000)));

    var underTest = HttpClientProvider.forTesting();

    var future = underTest.getHttpClient().getAsync(sonarqubeMock.url("/delayed"));
    assertThrows(TimeoutException.class, () -> future.get(100, TimeUnit.MILLISECONDS));
    assertThat(future.cancel(true)).isTrue();
    assertThat(future).isCancelled();

    assertThat(logTester.logs()).containsExactly("Request cancelled");
  }

  @Test
  void it_should_preserve_post_on_permanent_moved_status() {
    sonarqubeMock.stubFor(post("/afterMove").willReturn(aResponse()));
    sonarqubeMock.stubFor(post("/permanentMoved")
      .willReturn(aResponse()
        .withStatus(HttpStatus.SC_MOVED_PERMANENTLY)
        .withHeader("Location", sonarqubeMock.url("/afterMove"))));

    HttpClientProvider.forTesting().getHttpClient().post(sonarqubeMock.url("/permanentMoved"), "text/html", "Foo");

    sonarqubeMock.verify(postRequestedFor(urlEqualTo("/afterMove")));
  }

  @Test
  void it_should_preserve_post_on_temporarily_moved_status() {
    sonarqubeMock.stubFor(post("/afterMove").willReturn(aResponse()));
    sonarqubeMock.stubFor(post("/tempMoved")
      .willReturn(aResponse()
        .withStatus(HttpStatus.SC_MOVED_TEMPORARILY)
        .withHeader("Location", sonarqubeMock.url("/afterMove"))));

    HttpClientProvider.forTesting().getHttpClient().post(sonarqubeMock.url("/tempMoved"), "text/html", "Foo");

    sonarqubeMock.verify(postRequestedFor(urlEqualTo("/afterMove")));
  }

  @Test
  void it_should_preserve_post_on_see_other_status() {
    sonarqubeMock.stubFor(post("/afterMove").willReturn(aResponse()));
    sonarqubeMock.stubFor(post("/seeOther")
      .willReturn(aResponse()
        .withStatus(HttpStatus.SC_SEE_OTHER)
        .withHeader("Location", sonarqubeMock.url("/afterMove"))));

    HttpClientProvider.forTesting().getHttpClient().post(sonarqubeMock.url("/seeOther"), "text/html", "Foo");

    sonarqubeMock.verify(postRequestedFor(urlEqualTo("/afterMove")));
  }

  @Test
  void it_should_not_retry_non_idempotent_by_default() {
    sonarqubeMock.stubFor(post("/error").willReturn(aResponse().withStatus(HttpStatus.SC_SERVICE_UNAVAILABLE)));

    var underTest = HttpClientProvider.forTesting();

    underTest.getHttpClient().post(sonarqubeMock.url("/error"), ContentType.TEXT_PLAIN.getMimeType(), "body");

    sonarqubeMock.verify(1, postRequestedFor(urlEqualTo("/error"))
      .withHeader("User-Agent", equalTo("SonarLint tests")));
  }
}
