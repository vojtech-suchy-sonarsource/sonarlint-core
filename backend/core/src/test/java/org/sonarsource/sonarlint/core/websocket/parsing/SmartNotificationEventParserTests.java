/*
ACR-dc78447093c7457d9b0e454570976d6f
ACR-74b9d87eb00a4639a313d739377b9bbd
ACR-15518ddec3b34ba8a34427f7fc020531
ACR-8c847cbf2a674854ac967ecef7ef7288
ACR-2a47cfb154164421a34dbd2df787422f
ACR-ec152829032843f3a8000ec2ff32eabf
ACR-a071ab2b0f594819a234360d1731c2f2
ACR-865efee341234b2fa46fbd62cf6f470f
ACR-912a085b8ae84d03b61553b379e17f8d
ACR-f2eeaf44f0334d158cf99b39fa5977cd
ACR-498250caf0534da9a3cb4f2cd2fbb032
ACR-41db2b3d605a4f12b399249f0bde06cc
ACR-e697572b544941e79ecded043f01eff0
ACR-68cbf37350784cfc9bb731f283960cc8
ACR-3d80c3169b8a47ac8748141bfce7bc29
ACR-3b8d4f0b546d47a59fcceb1c5cfbedfd
ACR-baead78cea444ae78179251172285cd7
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
