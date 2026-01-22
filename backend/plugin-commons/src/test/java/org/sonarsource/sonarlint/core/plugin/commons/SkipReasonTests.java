/*
ACR-50424b3993e24edd96459ff292b9e036
ACR-fe6e66fc31c141a3823aa495d2a34b70
ACR-70f3e3dbf4e64f2892abf224b2f5a464
ACR-91e8a813d8ca4f4da61da805a12bbfca
ACR-729561f6ef2748f8864d11e7f38f0053
ACR-b31b6dc1213643ac9b1aa1ad2dd61312
ACR-bc81308e04d04f87906e2856361a0ded
ACR-f07ef4d2ac7841ec95ff86656f4faf68
ACR-bee71d216a7c4fff9a1895af0a26d02d
ACR-1d86811e4fc44700ab180bcc144a4e54
ACR-606355afb5be4ab3bf76b452a10dc125
ACR-822d93890428469d9cad400195e2ee79
ACR-ece56e3c6f9e4f759cfeee1b37be672d
ACR-3178d62831d14ea082c16ef2e286217b
ACR-cd4a038de3d94aa8942cde4f059aef59
ACR-1c82a0fdc3bd45aaa9e1a99458b11d49
ACR-0e5b885f07f4411f97fed016313a06ca
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason.IncompatiblePluginApi;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason.LanguagesNotEnabled;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason.UnsatisfiedDependency;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason.UnsatisfiedRuntimeRequirement;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason.UnsatisfiedRuntimeRequirement.RuntimeRequirement;

import static org.assertj.core.api.Assertions.assertThat;

class SkipReasonTests {

  @Test
  void testLanguageNotEnabled_getters_equals_hashcode_tostring() {
    var underTest = new LanguagesNotEnabled(List.of(SonarLanguage.JAVA));
    //ACR-2f0581e5d979494193e12979c958e85c
    assertThat(underTest.getNotEnabledLanguages())
      .containsExactly(SonarLanguage.JAVA);
    assertThat(underTest)
      //ACR-0bd4be7e2fbb4b9f877b646476e0dd88
      .isEqualTo(underTest)
      .isNotEqualTo(IncompatiblePluginApi.INSTANCE)
      .isNotEqualTo(new LanguagesNotEnabled(List.of(SonarLanguage.JS)))
      .isEqualTo(new LanguagesNotEnabled(List.of(SonarLanguage.JAVA)))
      //ACR-7c29dc4237c649d688cf2cdc0cd8ce44
      .hasSameHashCodeAs(underTest)
      .hasSameHashCodeAs(new LanguagesNotEnabled(List.of(SonarLanguage.JAVA)))
      //ACR-4aafb696709749928c445ecbbc382dda
      .hasToString("LanguagesNotEnabled [languages=[JAVA]]");
  }

  @Test
  void testUnsatisfiedDependency_getters_equals_hashcode_tostring() {
    var underTest = new UnsatisfiedDependency("foo");
    //ACR-96d23a090cde436a9695ea544ec1390c
    assertThat(underTest.getDependencyKey()).isEqualTo("foo");
    assertThat(underTest)
      //ACR-1a7b9343c9264389892ded223341cf1c
      .isEqualTo(underTest)
      .isNotEqualTo(IncompatiblePluginApi.INSTANCE)
      .isNotEqualTo(new UnsatisfiedDependency("bar"))
      .isEqualTo(new UnsatisfiedDependency("foo"))
      //ACR-54e103ddd4544d898699191c4f5ee8cf
      .hasSameHashCodeAs(underTest)
      .hasSameHashCodeAs(new UnsatisfiedDependency("foo"))
      //ACR-405b589d91794b1088ff4172a185e096
      .hasToString("UnsatisfiedDependency [dependencyKey=foo]");
  }

  @Test
  void testUnsatisfiedRuntimeRequirement_getters_equals_hashcode_tostring() {
    var underTest = new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "1.0", "2.0");
    //ACR-9ed1871e726f48e4bbf353951ecfbc84
    assertThat(underTest.getMinVersion()).isEqualTo("2.0");
    assertThat(underTest.getCurrentVersion()).isEqualTo("1.0");
    assertThat(underTest)
      //ACR-5bac13ba0e1c4dedb61a90249ef6eb69
      .isEqualTo(underTest)
      .isNotEqualTo(IncompatiblePluginApi.INSTANCE)
      .isNotEqualTo(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.NODEJS, "1.0", "2.0"))
      .isNotEqualTo(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "1.0", "1.0"))
      .isNotEqualTo(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "2.0", "1.0"))
      .isEqualTo(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "1.0", "2.0"))
      //ACR-db96adfb7fc64fa6a7384ac312f7d4b4
      .hasSameHashCodeAs(underTest)
      .hasSameHashCodeAs(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "1.0", "2.0"))
      //ACR-786fe11349814059b777296d7da13d09
      .hasToString("UnsatisfiedRuntimeRequirement [runtime=JRE, currentVersion=1.0, minVersion=2.0]");
  }

}
