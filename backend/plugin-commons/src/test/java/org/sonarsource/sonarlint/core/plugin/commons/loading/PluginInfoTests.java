/*
ACR-4c6132b05f4349e2af240d2b6d3bbeb6
ACR-d66f128f2ad64d2b8de4d21206679ddf
ACR-004d4408e3824c45aba1ce7c1d91e8bc
ACR-65f711e849a74f558b05a1c6a61f26ce
ACR-73dcdc8138504b4c853dfa710a8b6fdd
ACR-d471a11fc19e4a0f85e87d09db5423e0
ACR-a9a306af13984615b966dc4c5085aea8
ACR-c10f81fbbaeb4ef2b24cd29b97d75632
ACR-ffb419dcaf15442483509459d26f9c83
ACR-00dc3e37de04418ca15efcaaa2c4f222
ACR-87b941f522ab4106bef2a3a75e8d5940
ACR-1d0929607b774b0b8d1aa6c0b12b5a2a
ACR-2875319572274b5594d6f1fbef81423a
ACR-46515f26e7f34e4fb60774c89376f1c5
ACR-e5487f23614340aa8b89a7143c7bc919
ACR-5f805a7e87af42cc87fed431f9347ab4
ACR-2064aa4408cf488092e06a873502fc59
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.internal.apachecommons.io.FileUtils;
import org.sonar.api.utils.ZipUtils;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.plugin.commons.loading.SonarPluginManifest.RequiredPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PluginInfoTests {

  @Test
  void test_equals() {
    var java1 = new PluginInfo("java").setVersion(Version.create("1.0"));
    var java2 = new PluginInfo("java").setVersion(Version.create("2.0"));
    var javaNoVersion = new PluginInfo("java");
    var cobol = new PluginInfo("cobol").setVersion(Version.create("1.0"));

    assertThat(java1.equals(java1)).isTrue();
    assertThat(java1.equals(java2)).isFalse();
    assertThat(java1.equals(javaNoVersion)).isFalse();
    assertThat(java1.equals(cobol)).isFalse();
    assertThat(java1.equals("java:1.0")).isFalse();
    assertThat(java1.equals(null)).isFalse();
    assertThat(javaNoVersion.equals(javaNoVersion)).isTrue();

    assertThat(java1).hasSameHashCodeAs(java1);
    assertThat(javaNoVersion).hasSameHashCodeAs(javaNoVersion);
  }

  @Test
  void test_compatibility_with_sq_version() throws IOException {
    assertThat(withMinSqVersion("1.1").isCompatibleWith("1.1")).isTrue();
    assertThat(withMinSqVersion("1.1").isCompatibleWith("1.1.0")).isTrue();
    assertThat(withMinSqVersion("1.0").isCompatibleWith("1.0.0")).isTrue();

    assertThat(withMinSqVersion("1.0").isCompatibleWith("1.1")).isTrue();
    assertThat(withMinSqVersion("1.1.1").isCompatibleWith("1.1.2")).isTrue();
    assertThat(withMinSqVersion("2.0").isCompatibleWith("2.1.0")).isTrue();
    assertThat(withMinSqVersion("3.2").isCompatibleWith("3.2-RC1")).isTrue();
    assertThat(withMinSqVersion("3.2").isCompatibleWith("3.2-RC2")).isTrue();
    assertThat(withMinSqVersion("3.2").isCompatibleWith("3.1-RC2")).isFalse();

    assertThat(withMinSqVersion("1.1").isCompatibleWith("1.0")).isFalse();
    assertThat(withMinSqVersion("2.0.1").isCompatibleWith("2.0.0")).isTrue();
    assertThat(withMinSqVersion("2.10").isCompatibleWith("2.1")).isFalse();
    assertThat(withMinSqVersion("10.10").isCompatibleWith("2.2")).isFalse();

    assertThat(withMinSqVersion("1.1-SNAPSHOT").isCompatibleWith("1.0")).isFalse();
    assertThat(withMinSqVersion("1.1-SNAPSHOT").isCompatibleWith("1.1")).isTrue();
    assertThat(withMinSqVersion("1.1-SNAPSHOT").isCompatibleWith("1.2")).isTrue();
    assertThat(withMinSqVersion("1.0.1-SNAPSHOT").isCompatibleWith("1.0")).isTrue();

    assertThat(withMinSqVersion("3.1-RC2").isCompatibleWith("3.2-SNAPSHOT")).isTrue();
    assertThat(withMinSqVersion("3.1-RC1").isCompatibleWith("3.2-RC2")).isTrue();
    assertThat(withMinSqVersion("3.1-RC1").isCompatibleWith("3.1-RC2")).isTrue();

    assertThat(withMinSqVersion(null).isCompatibleWith("0")).isTrue();
    assertThat(withMinSqVersion(null).isCompatibleWith("3.1")).isTrue();

    assertThat(withMinSqVersion("7.0.0.12345").isCompatibleWith("7.0")).isTrue();
  }

  @Test
  void create_from_minimal_manifest(@TempDir Path temp) throws Exception {
    var manifest = mock(SonarPluginManifest.class);
    when(manifest.getKey()).thenReturn("java");
    when(manifest.getVersion()).thenReturn("1.0");
    when(manifest.getName()).thenReturn("Java");
    when(manifest.getMainClass()).thenReturn("org.foo.FooPlugin");

    var jarFile = temp.resolve("myPlugin.jar");
    var pluginInfo = PluginInfo.create(jarFile, manifest);

    assertThat(pluginInfo.getKey()).isEqualTo("java");
    assertThat(pluginInfo.getName()).isEqualTo("Java");
    assertThat(pluginInfo.getVersion().getName()).isEqualTo("1.0");
    assertThat(pluginInfo.getJarFile()).isEqualTo(jarFile.toFile());
    assertThat(pluginInfo.getMainClass()).isEqualTo("org.foo.FooPlugin");
    assertThat(pluginInfo.getBasePlugin()).isNull();
    assertThat(pluginInfo.getMinimalSqVersion()).isNull();
    assertThat(pluginInfo.getRequiredPlugins()).isEmpty();
    assertThat(pluginInfo.getJreMinVersion()).isNull();
    assertThat(pluginInfo.getNodeJsMinVersion()).isNull();
  }

  @Test
  void create_from_complete_manifest(@TempDir Path temp) throws Exception {
    var manifest = mock(SonarPluginManifest.class);
    when(manifest.getKey()).thenReturn("fbcontrib");
    when(manifest.getVersion()).thenReturn("2.0");
    when(manifest.getName()).thenReturn("Java");
    when(manifest.getMainClass()).thenReturn("org.fb.FindbugsPlugin");
    when(manifest.getBasePluginKey()).thenReturn("findbugs");
    when(manifest.getSonarMinVersion()).thenReturn(Optional.of(Version.create("4.5.1")));
    when(manifest.getRequiredPlugins()).thenReturn(List.of(new RequiredPlugin("java", Version.create("2.0")), new RequiredPlugin("pmd", Version.create("1.3"))));
    when(manifest.getJreMinVersion()).thenReturn(Optional.of(Version.create("11")));
    when(manifest.getNodeJsMinVersion()).thenReturn(Optional.of(Version.create("12.18.3")));

    var jarFile = temp.resolve("myPlugin.jar");
    var pluginInfo = PluginInfo.create(jarFile, manifest);

    assertThat(pluginInfo.getBasePlugin()).isEqualTo("findbugs");
    assertThat(pluginInfo.getMinimalSqVersion().getName()).isEqualTo("4.5.1");
    assertThat(pluginInfo.getRequiredPlugins()).extracting("key").containsOnly("java", "pmd");
    assertThat(pluginInfo.getJreMinVersion().getName()).isEqualTo("11");
    assertThat(pluginInfo.getNodeJsMinVersion().getName()).isEqualTo("12.18.3");
  }

  @Test
  void create_from_file() throws URISyntaxException {
    var checkstyleJar = Paths.get(getClass().getResource("/sonar-checkstyle-plugin-2.8.jar").toURI());
    var checkstyleInfo = PluginInfo.create(checkstyleJar);

    assertThat(checkstyleInfo.getName()).isEqualTo("Checkstyle");
    assertThat(checkstyleInfo.getMinimalSqVersion()).isEqualTo(Version.create("2.8"));
  }

  @Test
  void test_toString() throws Exception {
    var pluginInfo = new PluginInfo("java").setVersion(Version.create("1.1"));
    assertThat(pluginInfo).hasToString("[java / 1.1]");
  }

  @Test
  void fail_when_jar_is_not_a_plugin(@TempDir Path temp) throws IOException {
    //ACR-8f0c0ac2525d4ca69dffa21a9e1e0bfb
    var jarRootDir = Files.createTempDirectory(temp, "myPlugin").toFile();
    FileUtils.write(new File(jarRootDir, "META-INF/MANIFEST.MF"), "Build-Jdk: 1.6.0_15", StandardCharsets.UTF_8);
    var jar = temp.resolve("myPlugin.jar");
    ZipUtils.zipDir(jarRootDir, jar.toFile());

    var thrown = assertThrows(IllegalStateException.class, () -> PluginInfo.create(jar));
    assertThat(thrown).hasMessage("Error while reading plugin manifest from jar: " + jar.toAbsolutePath());
  }

  PluginInfo withMinSqVersion(@Nullable String version) {
    var pluginInfo = new PluginInfo("foo");
    if (version != null) {
      pluginInfo.setMinimalSqVersion(Version.create(version));
    }
    return pluginInfo;
  }
}
