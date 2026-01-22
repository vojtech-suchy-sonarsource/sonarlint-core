/*
ACR-a9095d8356de444dabd5b7c35700380d
ACR-d66275f86b294b11bd0350b04460f352
ACR-4d9ea46ee8f24d8fb9878f026df4da40
ACR-2ad176804e1b45ccb947f5525f02db14
ACR-746874f12af74051927e99a490ee8bfc
ACR-2d42c86a5e1d47098b6342de8b1590c4
ACR-8e72426ef564411fa3efaae0d8baa154
ACR-80a1c3f2e73f4002a70dfb511c7001b4
ACR-9ba314ecd99b42b49015de6a616e0d07
ACR-33b9074a89dc44569749179652c71713
ACR-ea67231cdef2420394658966668d6906
ACR-c53dffdbfd174316a2fc66a09a30fbeb
ACR-ee475d300db54d60a0fab51cc48de25c
ACR-778d285467e34150bf2055b7f9ce764a
ACR-c0c9d90b2c9b4c8baa28dffab132d243
ACR-083401bd980640919600a06173fe53c8
ACR-862ed69875b84a9b87c0a43de4ac5d4a
 */
package org.sonarsource.sonarlint.core.websocket.parsing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;

class SmartNotificationEventParserTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private SmartNotificationEventParser smartNotificationEventParser;

  @Test
  void should_parse_valid_json_date() {
    smartNotificationEventParser = new SmartNotificationEventParser("QA");
    var jsonData = "{\"message\": \"msg\", \"link\": \"lnk\", \"project\": \"projectKey\", \"date\": \"2023-07-19T15:08:01+0000\"}";

    var optionalEvent = smartNotificationEventParser.parse(jsonData);

    assertThat(optionalEvent).isPresent();
    var event = optionalEvent.get();
    assertThat(event.category()).isEqualTo("QA");
    assertThat(event.date()).isEqualTo("2023-07-19T15:08:01+0000");
    assertThat(event.message()).isEqualTo("msg");
    assertThat(event.project()).isEqualTo("projectKey");
    assertThat(event.link()).isEqualTo("lnk");
  }

  @Test
  void should_not_parse_invalid_json_date() {
    smartNotificationEventParser = new SmartNotificationEventParser("QA");
    var jsonData = "{\"invalid\": \"msg\", \"link\": \"lnk\", \"project\": \"projectKey\", \"date\": \"2023-07-19T15:08:01+0000\"}";

    var optionalEvent = smartNotificationEventParser.parse(jsonData);

    assertThat(optionalEvent).isEmpty();
  }

}
