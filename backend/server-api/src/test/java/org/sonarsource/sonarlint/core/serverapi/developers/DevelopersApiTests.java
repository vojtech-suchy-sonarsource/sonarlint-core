/*
ACR-707b3eac87a9459181fdb222fe10e51c
ACR-0a0465476dbb4baa85e8fd695fdc9cf3
ACR-37ee6805e2bc4ee6b37b404b6453b882
ACR-6bcbbd890f104af287ffb8383f9d2de8
ACR-022a9459cc004835ab317bb4cb247e50
ACR-07c85b96fb344d75ac9e760e6748ea2e
ACR-7d0f9060653c429abbb3116851d75cce
ACR-d313d995e3f0496d9a3605b9cd79d52d
ACR-570ae7635fd945259322d08bb6750632
ACR-7e36bdef98b14a808b1a9a0233c0d58f
ACR-c50f21db53b8431d8f4a3c60d5912626
ACR-f4a76ef05c0048869483c24cfa449d7a
ACR-695183671e9640ae949ce3e4bbc75d2b
ACR-a39b0ae0dbd440079698b3c5d5468da0
ACR-4a73d29f2ac843dd87e8dd0fcfd7e4c9
ACR-f8919b32d11e4b64b0efbdf7345b671e
ACR-68f37eb1c94a465a86a8e1ff725395fc
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
