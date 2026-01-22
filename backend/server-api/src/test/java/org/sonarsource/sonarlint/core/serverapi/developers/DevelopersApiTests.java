/*
ACR-ba32ab08637b49d886e17308614b6e3c
ACR-91719ab43df1445090ad339214908e1a
ACR-3eb0b3eb8d174e7a9a2ed929e8f3066f
ACR-92eecd5f6cf8497a96a2004e139a10f8
ACR-55aecab1dfb744558697c2d3fce48a28
ACR-2b53f2d3781142e2831cf127f7d4cbec
ACR-b2869dec0f1e48ec8bdbe08216445167
ACR-b846f53753a349499ae8c39225d48f5c
ACR-d37f18dd74ef4e9cb8c21706e0ee4d0d
ACR-a2b9216304174a63b03bf1445b1f16f0
ACR-424949af75914852a76ac49961ec9881
ACR-aeace6c2fde64fb08748500b4f0b4b10
ACR-570f19231fb4492a8c24d3d6803ae2cd
ACR-03e2bf181d2b4fb8aa67dd36b11650f5
ACR-002ce1310d1f4c999c805dcbc09a33dd
ACR-1823af74076a4b83ab3f83bd18298df4
ACR-0ae671580e5a40e8a3c7e1212d7acec9
 */
package org.sonarsource.sonarlint.core.serverapi.developers;

import java.time.ZonedDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class DevelopersApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  private DevelopersApi underTest;

  @BeforeEach
  void setUp() {
    underTest = new DevelopersApi(mockServer.serverApiHelper());
  }

  @Test
  void should_return_events_for_a_given_project_key() {
    mockServer.addStringResponse("/api/developers/search_events?projects=projectKey&from=2022-01-01T12%3A00%3A00%2B0000", "{\"events\": [" +
      "{" +
      "\"category\": \"cat\"," +
      "\"message\": \"msg\"," +
      "\"link\": \"lnk\"," +
      "\"project\": \"projectKey\"," +
      "\"date\": \"2022-01-01T08:00:00+0000\"" +
      "}" +
      "]" +
      "}");

    var events = underTest.getEvents(Map.of("projectKey", ZonedDateTime.parse("2022-01-01T12:00:00Z")), new SonarLintCancelMonitor());

    assertThat(events)
      .extracting("category", "message", "link", "projectKey", "time")
      .containsOnly(tuple("cat", "msg", "lnk", "projectKey", ZonedDateTime.parse("2022-01-01T08:00:00Z")));
  }

  @Test
  void should_return_no_event_if_a_field_is_missing_in_one_of_them() {
    mockServer.addStringResponse("/api/developers/search_events?projects=projectKey&from=2022-01-01T12%3A00%3A00%2B0000", "{\"events\": [" +
      "{" +
      "\"message\": \"msg\"," +
      "\"link\": \"lnk\"," +
      "\"project\": \"projectKey\"," +
      "\"date\": \"2022-01-01T08:00:00+0000\"" +
      "}" +
      "]" +
      "}");

    var events = underTest.getEvents(Map.of("projectKey", ZonedDateTime.parse("2022-01-01T12:00:00Z")), new SonarLintCancelMonitor());

    assertThat(events).isEmpty();
  }

  @Test
  void should_return_no_event_if_the_request_fails() {
    var events = underTest.getEvents(Map.of("projectKey", ZonedDateTime.parse("2022-01-01T12:00:00Z")), new SonarLintCancelMonitor());

    assertThat(events).isEmpty();
  }
}
