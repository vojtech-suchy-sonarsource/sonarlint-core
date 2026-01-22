/*
ACR-8ca443f7f1604ec49fb077c42efbacfe
ACR-c4337ab99a6e489fa99e19c1ad00095d
ACR-4980671324484ee6afba1a146a9b75b2
ACR-d5df5b517d794e959ddd7bbbbbb2fcce
ACR-918cc409c12947e5b591a67455759e31
ACR-cf5371ab0cb14eb78eca9d7bdc6c78f0
ACR-95374b764fde4909bdc792f349074f17
ACR-e71f45b4e22045c7be886976152a951a
ACR-cbb7d94feb0d4b8595e132987c7f0798
ACR-70175c81a02f4aa384fcd3fb56d7fa41
ACR-543272d4b9814d3daa56bcbb82b5c58c
ACR-104bdbbbb7b249748a489c3f78daccc4
ACR-3f008fd66b724516b3ee5402369927a9
ACR-673956e808d244b0b7ed5ff8bdace4b8
ACR-e1cf710a68dc443282870432727d97c2
ACR-6555d6a320cc408992b00db5f5be3265
ACR-ce82dc95b00444d4a4247bbfe7afcab4
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
