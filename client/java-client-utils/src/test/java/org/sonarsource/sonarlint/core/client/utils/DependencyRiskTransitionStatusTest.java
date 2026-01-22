/*
ACR-4720a53f6d2c4696a9ca471661f13287
ACR-297e3be4a4db49dbbfdb43d346dd7a4b
ACR-7ed7b4d19cac42159bc609ef868c07ff
ACR-c6950ee83ec645fcbd2e1015e2dd4372
ACR-77ddf651f2bb4a08bfbb47a68126da27
ACR-7f250022f19f4af4aef703a5f463cae2
ACR-dcaff85ea41f4503a918825cace2150a
ACR-be5273d3c7594f478abbc263510667ca
ACR-c72ad1cc9bf34c6790fdac2bacca59e0
ACR-f31ad8cf078a49568c55cde33c5d2426
ACR-1e707d77d29b423baa118e74b99d2644
ACR-e434e7f8690b40ee90b60a9cd9ea1aab
ACR-e5c4f1bbad2b4ce2bed8365673ef7217
ACR-cb35618c8a5b4613a6544ec755460d70
ACR-f31fd68224ad4599befe70c1517b41aa
ACR-20557069905c451e9cd76c67f57ac281
ACR-2b4125a367f9430e98834e06210b664a
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DependencyRiskTransitionStatusTest {
  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : DependencyRiskDto.Transition.values()) {
      var converted = DependencyRiskTransitionStatus.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }

  @Test
  void should_get_title() {
    assertThat(DependencyRiskTransitionStatus.SAFE.getTitle()).isEqualTo("Safe");
  }

  @Test
  void should_get_description() {
    assertThat(DependencyRiskTransitionStatus.FIXED.getDescription())
      .isEqualTo("This finding has been fixed.");
  }
}
