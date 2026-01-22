/*
ACR-cc77fb85455a4126a38af22177ef4b2b
ACR-5a6e5261ba2f46e48551cf98f39554bc
ACR-7a4a8def08fe4a3e8611519b68150b69
ACR-5ec654e3af4e4d92a6fbed7451c60221
ACR-e048122ddf6740f0ad91a325e66858a1
ACR-4c0ce8ad781c41c9b49bf873ecac86ae
ACR-e5897788e7774a55b2340a25dde3d0f0
ACR-7849027f3b024627adecce554222b195
ACR-091fbd34972d45a6a51cc6b566446488
ACR-07782ea588474e7eaa8e34ef9eb05361
ACR-d383e17e8f6640a59f4e9b39efe8bece
ACR-309867cb1234485fbeaa66b692f2f1b9
ACR-3b9ce300996a4c96ae26f021aafec23a
ACR-8ae7129490db426dacde1398355af166
ACR-e4150bb31349447889558163bf2aba76
ACR-17143056b6b641d4b7bddef293037563
ACR-e9b271126f3d41f08f5e7ccea3ef11b7
 */
package org.sonarsource.sonarlint.core.telemetry.gessie;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieEvent;
import org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieMetadata;
import org.sonarsource.sonarlint.core.telemetry.gessie.event.payload.MessagePayload;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieMetadata.GessieSource;
import static org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieMetadata.SonarLintDomain;

class GessieHttpClientTests {

  private static final String IDE_ENDPOINT = "/ide";

  private GessieHttpClient tested;

  @RegisterExtension
  static WireMockExtension mockGessie = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @BeforeEach
  void setUp() {
    tested = new GessieHttpClient(HttpClientProvider.forTesting(), mockGessie.baseUrl(), "value");
  }

  @Test
  void should_upload_accepted_payload() throws URISyntaxException, IOException {
    mockGessie.stubFor(post(IDE_ENDPOINT)
      .willReturn(aResponse().withStatus(202)));

    tested.postEvent(getPayload());

    var fileContent = getTestJson("GessieRequest");
    await().untilAsserted(() -> mockGessie.verify(postRequestedFor(urlEqualTo(IDE_ENDPOINT))
        .withHeader("x-api-key", new EqualToPattern("value"))
      .withRequestBody(equalToJson(fileContent))));
  }

  @Test
  void should_handle_400_error_gracefully() throws URISyntaxException, IOException {
    mockGessie.stubFor(post(IDE_ENDPOINT)
      .willReturn(aResponse().withStatus(400)));

    tested.postEvent(new GessieEvent(null, null));

    var invalidRequest = getTestJson("InvalidRequest");
    await().untilAsserted(() -> mockGessie.verify(postRequestedFor(urlEqualTo(IDE_ENDPOINT))
      .withHeader("x-api-key", new EqualToPattern("value"))
      .withRequestBody(equalToJson(invalidRequest))));
  }

  @Test
  void should_handle_403_error_gracefully() throws URISyntaxException, IOException {
    mockGessie.stubFor(post(IDE_ENDPOINT)
      .willReturn(aResponse().withStatus(403)));

    tested.postEvent(getPayload());

    var fileContent = getTestJson("GessieRequest");
    await().untilAsserted(() -> mockGessie.verify(postRequestedFor(urlEqualTo(IDE_ENDPOINT))
      .withHeader("x-api-key", new EqualToPattern("value"))
      .withRequestBody(equalToJson(fileContent))));
  }

  private String getTestJson(String fileName) throws URISyntaxException, IOException {
    var resource = Objects.requireNonNull(getClass().getResource("/response/gessie/GessieHttpClientTest/" + fileName + ".json"))
      .toURI();
    return Files.readString(Path.of(resource));
  }

  private static GessieEvent getPayload() {
    return new GessieEvent(
      new GessieMetadata(UUID.fromString("a36e25e8-5a92-4b5d-93b4-ba0045947b4c"),
        new GessieSource(SonarLintDomain.INTELLIJ),
        "Analytics.Test.TestEvent",
        "1761821877867",
        "0"),
      new MessagePayload("Test event", "test")
    );
  }
}
