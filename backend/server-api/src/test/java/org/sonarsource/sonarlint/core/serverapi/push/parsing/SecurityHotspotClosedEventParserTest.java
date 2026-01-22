/*
ACR-3716e869dd7d4a069c534cfa3677ddfe
ACR-ee1c10f6422a46a2945b429384eceecc
ACR-74210ac4fb014e0f8eafdf5711ba3062
ACR-866f107d43df46af918629ea314ed8db
ACR-83044fa5d8a143e9a06ddeee2671372f
ACR-a9573462d7214e3aaa58eae1dee3ffd2
ACR-043b58c6b44f417a8127f4146bbb673d
ACR-3a23df384fcd49e592f4a4a265eee89f
ACR-48d8e0fb07b94ae8939ae9645365e84c
ACR-bf4a04db21204c73a1ea266edc4a92b7
ACR-1b4ea29588af4903b258bf0df94dd3b3
ACR-8a70cbbfc58f47f1bfeb0ec3866282b2
ACR-7817c5853d9e4ba3b1f1b764e7a1db5d
ACR-9e2325e4b756477c9e6f779b60344305
ACR-222e82a9c0ae4c4184a02929436b8c7b
ACR-4b135b0a7b2b4d2788879bd9b751bd04
ACR-f7c463bb049649b98d462391065601e5
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityHotspotClosedEventParserTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  SecurityHotspotClosedEventParser parser = new SecurityHotspotClosedEventParser();
  private static final String TEST_PAYLOAD_WITHOUT_KEY = """
    {
      "projectKey": "test",
      "filePath": "/project/path/to/file"
    }""";

  private static final String TEST_PAYLOAD_WITHOUT_PROJECT_KEY = """
    {
      "key": "AYhSN6mVrRF_krvNbHl1",
      "filePath": "/project/path/to/file"
    }""";

  private static final String VALID_PAYLOAD = """
    {
      "key": "AYhSN6mVrRF_krvNbHl1",
      "projectKey": "test",
      "filePath": "/project/path/to/file"
    }""";

  @ParameterizedTest
  @ValueSource(strings = {TEST_PAYLOAD_WITHOUT_KEY, TEST_PAYLOAD_WITHOUT_PROJECT_KEY})
  void shouldReturnEmptyOptionalWhenPayloadIsInvalid(String invalidPayload) {
    var parseResult = parser.parse(invalidPayload);
    assertThat(parseResult).isEmpty();
  }

  @Test
  void shouldReturnChangeEventWhenPayloadIsValid() {
    var parsedResult = parser.parse(VALID_PAYLOAD);
    assertThat(parsedResult).isPresent();
    assertThat(parsedResult.get().getHotspotKey()).isEqualTo("AYhSN6mVrRF_krvNbHl1");
    assertThat(parsedResult.get().getProjectKey()).isEqualTo("test");
    assertThat(parsedResult.get().getFilePath()).isEqualTo(Path.of("/project/path/to/file"));
  }
}
