/*
ACR-d885c0ac2b354ea88f738949432cb320
ACR-e9ced5cc247643ecab923eabb17670a1
ACR-8037fd5c2fac42648dd9bfb838aba91a
ACR-d0ecdf01dce54959927d885311b70b99
ACR-a14f140a056245acb7f1c37cb8338ecd
ACR-fb2b8790ac9b4db1bbb83d755f36c061
ACR-6a0aff4857c24c7f9472206a4fe8ec6c
ACR-e8aba8ab83794a2e863a8acfca15f5ba
ACR-67f44858620c4305b4500b55c365ec91
ACR-2cb2c83c58184a3380ef739e01d3cb4e
ACR-7abf7b3035514c90bd89bb9c1b495bcf
ACR-085a53f1d74e4e1381503e4018bb09e1
ACR-6f057f20b9174a428f08d798366ad134
ACR-1560b5f158d441e08cb1a2f414f03bab
ACR-2cec36a50bb94365babd971eb06277dd
ACR-881c2728a0c64b2785fae50387a1a97a
ACR-d991878f9a2447b4b779562b4b4555c1
 */
package org.sonarsource.sonarlint.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserPathsTests {

  @Test
  void default_home_should_be_in_user_home() {
    assertThat(UserPaths.computeUserHome(null)).isEqualTo(Paths.get(System.getProperty("user.home")).resolve(".sonarlint"));
  }

  @Test
  void env_setting_should_override_default_home() {
    assertThat(UserPaths.computeUserHome("clientPath")).isEqualTo(Paths.get("clientPath"));
  }

  @Test
  void should_return_telemetry_home() {
    var initializeParams = mock(InitializeParams.class);
    when(initializeParams.getSonarlintUserHome()).thenReturn("~/.sonarlint");
    when(initializeParams.getTelemetryConstantAttributes()).thenReturn(
      new TelemetryClientConstantAttributesDto("eclipse", "---", "1.2.3", "4.5.6", null));

    var userPaths = UserPaths.from(initializeParams);
    var telemetryDir = userPaths.getHomeIdeSpecificDir("telemetry");

    assertThat(telemetryDir)
      .isEqualTo(Path.of("~/.sonarlint/telemetry/eclipse"));
  }
}
