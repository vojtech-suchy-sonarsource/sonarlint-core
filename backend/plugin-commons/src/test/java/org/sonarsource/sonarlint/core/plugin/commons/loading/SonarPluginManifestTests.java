/*
ACR-41d919ae90094e959e1f5bdd72cdcc50
ACR-5e1cc6223daf42f3a9201ffcb684381b
ACR-7e54d66fe41b41c092cffe16d25164fb
ACR-0db42595b3434cf18939a62edeb1a19e
ACR-efa1bd1b0c3f438cb96e378fb6bc11dc
ACR-db41d1ae689e410f810f520188324c05
ACR-7c0e9cd5feee45a8b6a282325e50d12d
ACR-62cc498f28544dd19e69f1ba0137fd5f
ACR-6503c228ce5a46c2a6f700f5d5d27208
ACR-27ba851e92f44a56916927078439f93d
ACR-c9cf89e2b7fa459cbfc048c6c4d04c1d
ACR-4959f628b9b54bc8b7d449580d0fc8e7
ACR-67a7e891e9974e5eb0f7b1b465f967e8
ACR-3efaf091e3b043b1950dea96fdb55f35
ACR-8546ff3c52134d3dbecbfd7de27ec87c
ACR-6c79975a5612448694e566c69c895719
ACR-2da082e322a94426a82cf72a30a2ff60
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
