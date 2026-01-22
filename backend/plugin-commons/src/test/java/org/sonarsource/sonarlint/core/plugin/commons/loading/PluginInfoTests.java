/*
ACR-acdab2df2ec34faf9810720b4dee0c4e
ACR-57e4539cd5c2473fabdb62533ba8d724
ACR-a97a6c28fdc549c8880d8002803431f1
ACR-950903567d4e4c5c8709dc996bc7b1fd
ACR-e018d3d818ff404384800d94994fa4ee
ACR-5c4040350d5c4146a23229a0e1ace6a9
ACR-c578115940074837abd236cd45510f39
ACR-1876863d48ee4312a6e7f1dcd95e85d3
ACR-7b8d2984f1a24aad940468cca9f26625
ACR-882856a3734148218f5522b79a798953
ACR-c10f943b5af04e2bb11ffeb51b802a1c
ACR-7da0deb475194a848d379b3f5e4bf006
ACR-44454f1d4fb0418ba942c334fef43aa1
ACR-461d5ecf03544a8086e83099dc2d5009
ACR-5874beaf09084152b01ef4034f432bad
ACR-c68ebd38ec754a8dbe74f31987723822
ACR-ec48d472fe354120a012d5a24c748c87
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
    //ACR-b192e75302a54f66b02516e4d2261ad5
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
