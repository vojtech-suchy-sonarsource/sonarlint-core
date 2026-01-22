/*
ACR-c5f23509de4b4903b9fe9d3729fd1a7c
ACR-b6bcd360415343ff8e8b8f8c0b74a555
ACR-25acc6e2535a44d285c6d917081fd5f9
ACR-a6e464f2558c4b21ab1d629dc34c2a07
ACR-0f25b9e19dce4890b20e73c30c1236f4
ACR-3322bb4a867c4528a9ee7ff551368e80
ACR-df8929875dcf4d9caeac0881b96b85a2
ACR-e785d880dbac4d78a0b25555276e0e45
ACR-4361a2aced8043179818a6718cb1fe85
ACR-cdbc884b775d42a8b7b47842a88cb35d
ACR-f11038e45d2b46cf9d163d5dff380014
ACR-3b6ca48ab575480384c8124b1e7e3c92
ACR-d0b2a56976244032994f1be9f530e731
ACR-fd015ff335bd48c2872e97e5e1510ad6
ACR-d6eb004708414ca599d112f231304439
ACR-20045dc40ec1406bbbf7e3459cd3a1f7
ACR-6cd22762615249ffb3f5b001b55561df
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.plugin.commons.loading.SonarPluginManifest.RequiredPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SonarPluginManifestTests {

  @Test
  void test_RequiredPlugin() throws Exception {
    var plugin = SonarPluginManifest.RequiredPlugin.parse("java:1.1");
    assertThat(plugin.getKey()).isEqualTo("java");
    assertThat(plugin.getMinimalVersion().getName()).isEqualTo("1.1");

    assertThrows(IllegalArgumentException.class, () -> SonarPluginManifest.RequiredPlugin.parse("java"));
  }

  @Test
  void test() {
    var fake = Paths.get("fake.jar");
    assertThrows(RuntimeException.class, () -> SonarPluginManifest.fromJar(fake));
  }

  @Test
  void should_create_manifest_from_jar() throws URISyntaxException, IOException {
    var checkstyleJar = Paths.get(getClass().getResource("/sonar-checkstyle-plugin-2.8.jar").toURI());
    var manifest = SonarPluginManifest.fromJar(checkstyleJar);

    assertThat(manifest.getKey()).isEqualTo("checkstyle");
    assertThat(manifest.getName()).isEqualTo("Checkstyle");
    assertThat(manifest.getRequiredPlugins()).isEmpty();
    assertThat(manifest.getMainClass()).isEqualTo("org.sonar.plugins.checkstyle.CheckstylePlugin");
    assertThat(manifest.getVersion().length()).isGreaterThan(1);
    assertThat(manifest.getJreMinVersion()).isEmpty();
    assertThat(manifest.getNodeJsMinVersion()).isEmpty();
  }

  @Test
  void should_add_requires_plugins() throws URISyntaxException, IOException {
    var jar = getClass().getResource("/SonarPluginManifestTests/plugin-with-require-plugins.jar");

    var manifest = SonarPluginManifest.fromJar(Paths.get(jar.toURI()));

    assertThat(manifest.getRequiredPlugins())
      .usingRecursiveFieldByFieldElementComparator()
      .containsExactlyInAnyOrder(new RequiredPlugin("scm", Version.create("1.0")), new RequiredPlugin("fake", Version.create("1.1")));
  }

  @Test
  void should_parse_jre_min_version() throws URISyntaxException, IOException {
    var jar = getClass().getResource("/SonarPluginManifestTests/plugin-with-jre-min.jar");

    var manifest = SonarPluginManifest.fromJar(Paths.get(jar.toURI()));

    assertThat(manifest.getJreMinVersion()).contains(Version.create("11"));
  }

  @Test
  void should_default_jre_min_version_to_null() throws URISyntaxException, IOException {
    var jar = getClass().getResource("/SonarPluginManifestTests/plugin-without-jre-min.jar");

    var manifest = SonarPluginManifest.fromJar(Paths.get(jar.toURI()));

    assertThat(manifest.getJreMinVersion()).isEmpty();
  }

  @Test
  void should_parse_nodejs_min_version() throws URISyntaxException, IOException {
    var jar = getClass().getResource("/SonarPluginManifestTests/plugin-with-nodejs-min.jar");

    var manifest = SonarPluginManifest.fromJar(Paths.get(jar.toURI()));

    assertThat(manifest.getNodeJsMinVersion()).contains(Version.create("12.18.3"));
  }
}
