/*
ACR-79a1cf16f2bf4aeab06905d7518af852
ACR-84573c428d39425ea968c6c6a55e01e0
ACR-4c75897ff5184fc898c678bf840ff3e3
ACR-880c0b38e6a7484a936ccecdc166d174
ACR-a824ea08e75b4bf5bf33d37e3c8e9b14
ACR-924b1a9c98324812bb9c4ef04e769577
ACR-492f39abfd4b4015813b18209d715a43
ACR-7f4a24d713c34b7cbac994c6a255a14f
ACR-a4488965a3384d7d9ac2ab29f35a2298
ACR-f5be6eda79964be5ac965854542b93fd
ACR-de85690464044e7cba909266085d2f5c
ACR-dc8edcac4c0b4fd185e97a3efe975ea2
ACR-05b434a810414d7ebf1de2f13a31c803
ACR-7b1ca4b2e1ea4bd5a565705ca49d3331
ACR-46816f85534e4581bcdd1394a2918d6d
ACR-fcbc87bbea31494b96713645aaa83ed3
ACR-b6fcb8f2def440bf91937ab78a84e8b7
 */
package org.sonarsource.sonarlint.core.telemetry.gessie.event;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieMetadata.SonarLintDomain;

class GessieMetadataTests {

  @ParameterizedTest
  @MethodSource
  void should_map_product_key_to_domain(String productKey, SonarLintDomain expected) {
    var actual = SonarLintDomain.fromProductKey(productKey);

    assertThat(actual).isEqualTo(expected);
  }

  public static Stream<Arguments> should_map_product_key_to_domain() {
    return Stream.of(
      Arguments.of("idea", SonarLintDomain.INTELLIJ),
      Arguments.of("eclipse", SonarLintDomain.ECLIPSE),
      Arguments.of("visualstudio", SonarLintDomain.VISUAL_STUDIO),
      Arguments.of("vscode", SonarLintDomain.VS_CODE),
      Arguments.of("cursor", SonarLintDomain.VS_CODE),
      Arguments.of("windsurf", SonarLintDomain.VS_CODE),
      Arguments.of("", SonarLintDomain.SLCORE),
      Arguments.of("test", SonarLintDomain.SLCORE)
    );
  }
}
