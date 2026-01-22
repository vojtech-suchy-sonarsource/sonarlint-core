/*
ACR-6b14a5a2eca24bfca4d30985954c11c5
ACR-5d738aeef6e741658d8199a4af143ae4
ACR-312e37769dd24c5888dfc7e463a58c72
ACR-f00d1c588b994252a926fcc894507e89
ACR-312bf029b7c04c5a904be611469d0449
ACR-50b687c19543400fb72f316956540264
ACR-e967ae7fb4094a43bfb25c746603666b
ACR-8f4a9fe8b299485aa850f0b7743121ab
ACR-067f7e7329054b14b0261041fc346a9a
ACR-39f537a13b2c46c88a19b12ea621cde6
ACR-f2143b11a7174c80a0ef161d873c9c70
ACR-61b1a06617da4a8eb87607186e43caab
ACR-aa9ba8fcec704085b9b87939bc040ad9
ACR-ac90dca53a584763b7b4760af42c0829
ACR-27d4428273f948fca48b58ed985d0a79
ACR-9d07e2338e084a5b8988bf7db71671b4
ACR-0f6894a78a5b449596e3fd063f4c785a
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
