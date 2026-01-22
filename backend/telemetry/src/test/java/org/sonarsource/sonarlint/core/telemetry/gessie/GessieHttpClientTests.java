/*
ACR-18a9779b1710416a88bb059f54b380a8
ACR-b491312984f64c42a0bd679a8231a6f6
ACR-38a55bd5582a40dbb6b0568dacb56bf0
ACR-ddf19926f8f049078931725624e1ab51
ACR-83e2a578a6354189b9f3af0e84c394b3
ACR-574491c46b974f21844f266fad8b8608
ACR-f46f754ebcaa4d2bbc696db055e81428
ACR-2d8a7379b73b45dcaa0daac1d7dfef85
ACR-b603b3de001d4c84a8736535ff143301
ACR-a5dc45174aca48d29912cb7996c3ce65
ACR-349577b8375a46f8bd706881d7f47ba1
ACR-239fed0f55c54a4884d901eca03008f4
ACR-232b72d2cae247218844dd45979952db
ACR-d37b27463efc4a18940272175eb9b89a
ACR-ae1d67a6dd534a2aa825d04242c012ee
ACR-476c8ab5b7fe4edd8a5828b27ba6fced
ACR-a95985f6a0a04bc3b010538df6994741
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
