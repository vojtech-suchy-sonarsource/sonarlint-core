/*
ACR-ba9b8c955d194f8a80ab297259738548
ACR-307803a7d0134f97802b72c72de87a97
ACR-e5cb719d83274232a44e88d915069723
ACR-bd3869fdf44d4ee4944d046f0cda7075
ACR-22d668257d8e4d0da2aaad18d43032da
ACR-96dfe573eff74d96b3fb5833794238c6
ACR-9c70a5cae4cf4722815c3ec8c7991d7c
ACR-0fc9b405dcef4420a073ea572e77aae6
ACR-10d9f6778c914ae69259b396db39535c
ACR-cc3625cf52464fe891fda7437c8dbd9c
ACR-0c6ba1c5c88744c18efced18359317ab
ACR-92d35c72ae8b40ddb5ba8fc050781b45
ACR-ac21da83314b428492f1436de6bc1d44
ACR-cee50e9da4034536a3a85d8747ee3201
ACR-79328446916d4b2bb7dd436e3121be06
ACR-6c3f8140e437420bb889d5e548d53146
ACR-bd275f948a1044db9b55fbd434ee8f73
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.commons.HotspotReviewStatus.ACKNOWLEDGED;
import static org.sonarsource.sonarlint.core.commons.HotspotReviewStatus.SAFE;
import static org.sonarsource.sonarlint.core.commons.HotspotReviewStatus.TO_REVIEW;

class SecurityHotspotChangedEventParserTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  SecurityHotspotChangedEventParser parser = new SecurityHotspotChangedEventParser();
  private static final String TEST_PAYLOAD_WITHOUT_KEY = """
    {
      "projectKey": "test",
      "updateDate": 1685007187000,
      "status": "REVIEWED",
      "assignee": "AYfcq2moStCcBwCPm0uK",
      "resolution": "ACKNOWLEDGED",
      "filePath": "/project/path/to/file"
    }""";

  private static final String TEST_PAYLOAD_WITHOUT_PROJECT_KEY = """
    {
      "key": "AYhSN6mVrRF_krvNbHl1",
      "updateDate": 1685007187000,
      "status": "REVIEWED",
      "assignee": "AYfcq2moStCcBwCPm0uK",
      "resolution": "ACKNOWLEDGED",
      "filePath": "/project/path/to/file"
    }""";

  private static final String TEST_PAYLOAD_WITHOUT_FILE_PATH = """
    {
      "key": "AYhSN6mVrRF_krvNbHl1",
      "projectKey": "test",
      "updateDate": 1685007187000,
      "status": "REVIEWED",
      "assignee": "AYfcq2moStCcBwCPm0uK",
      "resolution": "ACKNOWLEDGED"
    }""";

  private static final String VALID_PAYLOAD = """
    {
      "key": "AYhSN6mVrRF_krvNbHl1",
      "projectKey": "test",
      "updateDate": 1685007187000,
      "status": "REVIEWED",
      "assignee": "assigneeEmail",
      "resolution": "ACKNOWLEDGED",
      "filePath": "/project/path/to/file"
    }""";

  @ParameterizedTest
  @ValueSource(strings = {TEST_PAYLOAD_WITHOUT_KEY, TEST_PAYLOAD_WITHOUT_PROJECT_KEY, TEST_PAYLOAD_WITHOUT_FILE_PATH})
  void shouldReturnEmptyOptionalWhenPayloadIsInvalid(String invalidPayload) {
    var parseResult = parser.parse(invalidPayload);
    assertThat(parseResult).isEmpty();
  }

  @Test
  void shouldReturnChangeEventWhenPayloadIsValid() {
    var parsedResult = parser.parse(VALID_PAYLOAD);
    assertThat(parsedResult).isPresent();
    assertThat(parsedResult.get().getAssignee()).isEqualTo("assigneeEmail");
    assertThat(parsedResult.get().getHotspotKey()).isEqualTo("AYhSN6mVrRF_krvNbHl1");
    assertThat(parsedResult.get().getStatus()).isEqualTo(ACKNOWLEDGED);
    assertThat(parsedResult.get().getProjectKey()).isEqualTo("test");
    assertThat(parsedResult.get().getFilePath()).isEqualTo(Path.of("/project/path/to/file"));
  }

  @Test
  void shouldCorrectlyMapStatus() {
    var payloadNoResolution = """
      {
        "key": "AYhSN6mVrRF_krvNbHl1",
        "projectKey": "test",
        "updateDate": 1685007187000,
        "status": "TO_REVIEW",
        "assignee": "assigneeEmail",
        "filePath": "/project/path/to/file"
      }""";

    var parsedResult = parser.parse(payloadNoResolution);
    assertThat(parsedResult).isPresent();
    assertThat(parsedResult.get().getStatus()).isEqualTo(TO_REVIEW);

    var payloadSafe = """
      {
        "key": "AYhSN6mVrRF_krvNbHl1",
        "projectKey": "test",
        "updateDate": 1685007187000,
        "status": "REVIEWED",
        "assignee": "assigneeEmail",
        "resolution": "SAFE",
        "filePath": "/project/path/to/file"
      }""";

    var parsedResult2 = parser.parse(payloadSafe);
    assertThat(parsedResult2).isPresent();
    assertThat(parsedResult2.get().getStatus()).isEqualTo(SAFE);
  }
}
