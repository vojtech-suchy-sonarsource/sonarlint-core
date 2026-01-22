/*
ACR-dedf4be32abc484cbf4921f16c9b3fa2
ACR-a84cbfebd66a46e08f6eedc26e483b2d
ACR-e745d7b42c1c41968ef8a19de9bbc63a
ACR-9dcbeaba547942049605767a82dee7cc
ACR-1bf19aacbc4c4e18a2943aff36067fc1
ACR-9a5a2a6c6a7b4a92b1f67588bc94ed2f
ACR-774c479537104daf86e287b41af2befa
ACR-cd8a8123c4aa49b394c0c79dd216b9e3
ACR-ef511cebd50a4b778a44bcb00c54f684
ACR-cab152b54fbe46a6936d7ba12ccb8b3f
ACR-e19cfdcfd8c340a5a0eac930a4969bf6
ACR-e83c0602644f426fb7268cd625a5e27f
ACR-abc9e3c5d9274aa6b30c7623c2afb209
ACR-bae506924d9b4d3ab4e61ed3d7b0bcf6
ACR-2733ec9e415c407f900394075e7d4c5e
ACR-41a8c581db3e49548fc2d7d34e8a14e9
ACR-919636384cbc4dada132bf3f3816db33
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
