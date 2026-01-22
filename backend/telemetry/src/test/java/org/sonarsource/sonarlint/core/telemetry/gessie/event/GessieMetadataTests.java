/*
ACR-115d69264727426f9458d2cc045101a2
ACR-29126baa490f45189205011a42676d32
ACR-8cb69a365655452dbdbac2be4b3aec11
ACR-560de5860ae34a8fa5e2b2003bbdedc2
ACR-6d0098a94f664b9e989bbd8d09dc691a
ACR-edf9c22ae1fd4c439b0813edf60939f6
ACR-7e97bc0bcfaa4561a12f7e8a4592001d
ACR-19a97cd022784f348f7bff7910107c35
ACR-ce67ac5911c84e1f878365f77b677c37
ACR-721873a4c9f745c293e5a1538602d327
ACR-35d94949ebb340a288cb0a2b1451d123
ACR-ce39a3a7ae1b40f28b78e8bf24e2dddc
ACR-80d9d32385ac4ac995c33500710d32bf
ACR-99bceaecb8d04b198fb92e286ca06ddd
ACR-86c0317eaa424e88b6bd2eeb0cdd3fd8
ACR-866e2f438d1f48d8bbdbec9cbab0201b
ACR-6d98d72072134dcf8cc4a354193d782e
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
