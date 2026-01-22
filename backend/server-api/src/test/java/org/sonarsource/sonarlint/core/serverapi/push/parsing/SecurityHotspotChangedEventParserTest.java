/*
ACR-a987c404c98549a3b83c05bd9ac0a389
ACR-b33fab159602457e9be25c64dfe2b982
ACR-d3a2d428d40246cdbf32e929474d63fd
ACR-fdccdc1c7f024627a3c630d3f243a5ec
ACR-1b4d65f47cec42df8455cf08e780a1ce
ACR-0fb6d8285e304e0da3fb88dbac16bb42
ACR-88ac662756664a8fa30e848029b837b2
ACR-50680babf13342dda6236f2a8929e903
ACR-54d11d79fff64231bcccf50d9e5ced5f
ACR-23df0d83731b4c888ec3273c48900fce
ACR-555e9c94e77347c7a4f3303a8d7e581c
ACR-0b8b010e35254297831448350368f4d6
ACR-e10c56d3d5a944a5b12979b61db835e2
ACR-878aab58c77a496a8dc2de4c6bc48ccd
ACR-967134188a554b90a7958d9ae1787b7b
ACR-161201dc27c74cd29eaa0fd775c6b0ed
ACR-529f375d0913430ab7faa30b61999daf
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
