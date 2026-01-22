/*
ACR-e768b6806b654d9f843c13faa826d914
ACR-4af2c8201aa4444d81457c545841ee25
ACR-7efd6bdf50654073971eddf1dd6e4abc
ACR-46e77630a6424a44977b0aeab27561c6
ACR-99dfe731aa374e729a8a273867526919
ACR-f2d6f8afa75d423cab97a21bef546b13
ACR-a443a51cf379444489a0fb9132a5a4b3
ACR-eefdf42da8b04cd18b8159621dd26739
ACR-cc673309e7f34a21aa6775f2a5204952
ACR-d9b2d8bd26484ca0a723a1614d4230b0
ACR-cca7b6d4cedf4fe09d24ec0a49cb1295
ACR-164ddab745194b32ae1656748124001d
ACR-33920c6b7aed40efa91d735892e888ba
ACR-a2634dbb948c406caf29fc9c030467ad
ACR-3e829e570182477ca9048757a1682b01
ACR-be488ccf6f254a24a9b4e639d88e100f
ACR-54dbb70fe7fe46fc9c13af0c0435522b
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
    //ACR-1c758187a222437db0ff9d9e93184321
    assertThat(underTest.getNotEnabledLanguages())
      .containsExactly(SonarLanguage.JAVA);
    assertThat(underTest)
      //ACR-03912d337a8e42e1bd0297b053a5d83a
      .isEqualTo(underTest)
      .isNotEqualTo(IncompatiblePluginApi.INSTANCE)
      .isNotEqualTo(new LanguagesNotEnabled(List.of(SonarLanguage.JS)))
      .isEqualTo(new LanguagesNotEnabled(List.of(SonarLanguage.JAVA)))
      //ACR-685cc7e3bc4341eb8af6a1a6c4da6a8f
      .hasSameHashCodeAs(underTest)
      .hasSameHashCodeAs(new LanguagesNotEnabled(List.of(SonarLanguage.JAVA)))
      //ACR-b79b6d5b872e425f819e8529254f9a61
      .hasToString("LanguagesNotEnabled [languages=[JAVA]]");
  }

  @Test
  void testUnsatisfiedDependency_getters_equals_hashcode_tostring() {
    var underTest = new UnsatisfiedDependency("foo");
    //ACR-13a20639874b4fa18fb1467953ca2b18
    assertThat(underTest.getDependencyKey()).isEqualTo("foo");
    assertThat(underTest)
      //ACR-023d5cd73c4b464193f018adf28e0ae8
      .isEqualTo(underTest)
      .isNotEqualTo(IncompatiblePluginApi.INSTANCE)
      .isNotEqualTo(new UnsatisfiedDependency("bar"))
      .isEqualTo(new UnsatisfiedDependency("foo"))
      //ACR-a1e4bad9ff0949ffbf975af3c42a64ca
      .hasSameHashCodeAs(underTest)
      .hasSameHashCodeAs(new UnsatisfiedDependency("foo"))
      //ACR-742755a951594265bda8f4ef11bb8a16
      .hasToString("UnsatisfiedDependency [dependencyKey=foo]");
  }

  @Test
  void testUnsatisfiedRuntimeRequirement_getters_equals_hashcode_tostring() {
    var underTest = new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "1.0", "2.0");
    //ACR-383e086268f24428b5a9bcae3a485f49
    assertThat(underTest.getMinVersion()).isEqualTo("2.0");
    assertThat(underTest.getCurrentVersion()).isEqualTo("1.0");
    assertThat(underTest)
      //ACR-d85c424504be458ab3d0b8a905845b34
      .isEqualTo(underTest)
      .isNotEqualTo(IncompatiblePluginApi.INSTANCE)
      .isNotEqualTo(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.NODEJS, "1.0", "2.0"))
      .isNotEqualTo(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "1.0", "1.0"))
      .isNotEqualTo(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "2.0", "1.0"))
      .isEqualTo(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "1.0", "2.0"))
      //ACR-630b7c5716194943b877ebe7d72278d8
      .hasSameHashCodeAs(underTest)
      .hasSameHashCodeAs(new UnsatisfiedRuntimeRequirement(RuntimeRequirement.JRE, "1.0", "2.0"))
      //ACR-019f0d1af2da4105871126c77863fc2a
      .hasToString("UnsatisfiedRuntimeRequirement [runtime=JRE, currentVersion=1.0, minVersion=2.0]");
  }

}
