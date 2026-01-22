/*
ACR-bafacd0306214e85b2ca679ce8df92e2
ACR-32c612378e8c4a728249724a073f305c
ACR-17ee4ac7b40c4d66aa0820d171f6714f
ACR-4b275decb4d34074b2d659e8185097f6
ACR-e3a3d2c53d064922b385178acd6d6323
ACR-4c8a127c96ee4ed3beac30046630b592
ACR-f98c583289724f04b2f09116c5e8795d
ACR-6129a7d8b9b840d8a4044fe245aa8bca
ACR-26d39ef213ce41dd9455fca6f96ea658
ACR-cbb5858795da497f801e0cdd456c122b
ACR-6113a66d675142afa7dc452ff8a77e12
ACR-d2283697e9d84183a4d5f17ebe2df441
ACR-69f0901ec371427bba9ad10cd0590f95
ACR-c03c79f0481f44cf86846d14c65956b1
ACR-3c8f0bf1ea1e4275a02b00b9ef50f0ce
ACR-15ae49c27e71431ea75df97f002cee69
ACR-7bd0c7f1f0ca4ab9b118bcb8b0ce231d
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
