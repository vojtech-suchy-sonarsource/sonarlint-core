/*
ACR-3e8ec25d9fa94cf4aabf55a52939a813
ACR-7a239541ee844cfbba1a9fb15bb5ff95
ACR-bbce76a97c0b493eb9c936c5dc30431d
ACR-548bb4df533840359f20e96d109e49a1
ACR-3a43cb5a74124840848cca0d5f8fb604
ACR-35894281cfb84567829024ea91c03395
ACR-ec56006b14ef4e398870322a8651a12e
ACR-aed372ee05d9436d9b246a31d8e9d596
ACR-6b49eb35dfce4571ad0c0b764e62d41f
ACR-658d565f7af0493ea23613d503db7212
ACR-88b726b35973493087f035de9479b42b
ACR-a671e1e7948342e888d9d07fd66c5bff
ACR-1d758b65206c47c9b147a51af4942769
ACR-6e9addb26a4f4652a7f437870238fcd6
ACR-0874266c486947fa9040766d8f1ac9fb
ACR-b90e4a936f674f24a07d84358cac519d
ACR-72eca9cf863e43b79ed99f61b3268e91
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.net.URL;
import org.junit.jupiter.api.Test;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ApiVersionsTests {
  @Test
  void can_load_sonar_plugin_api_version_from_embedded_resource() {
    var version = ApiVersions.loadSonarPluginApiVersion();

    assertThat(version).isNotNull();
    assertThat(version.isGreaterThanOrEqual(Version.create(8, 5))).isTrue();
  }

  @Test
  void can_load_sonarlint_plugin_api_version_from_embedded_resource() {
    var version = ApiVersions.loadSonarLintPluginApiVersion();

    assertThat(version).isNotNull();
    assertThat(version.isGreaterThanOrEqual(Version.create(5, 4))).isTrue();
  }

  @Test
  void should_throw_an_exception_if_resource_does_not_exist() {
    var throwable = catchThrowable(() -> ApiVersions.loadVersion(null, "wrongPath"));

    assertThat(throwable).hasMessage("Can not load wrongPath from classpath");
  }

  @Test
  void should_throw_an_exception_if_resource_can_not_be_loaded() {
    var throwable = catchThrowable(() -> ApiVersions.loadVersion(new URL("file://wrong"), "wrongPath"));

    assertThat(throwable).hasMessage("Can not load wrongPath from classpath");
  }

}
