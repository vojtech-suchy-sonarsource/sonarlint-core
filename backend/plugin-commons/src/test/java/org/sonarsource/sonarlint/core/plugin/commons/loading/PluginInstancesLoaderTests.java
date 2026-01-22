/*
ACR-176e597501404b469de5e0d787451ba4
ACR-c8ce9ee115f049508266ab99e801165b
ACR-ea66426b5bc24ca4be7ac195e9377e50
ACR-9b5b47c6799c4ab1a488db5d3471eb35
ACR-f2ea8361a6e741e682b0017943317beb
ACR-d7836296230f4008ab5b35898c540d47
ACR-e91b6beb0d494a82a120feae5778aed2
ACR-13580222640d413e91546ab1fb15b8ae
ACR-60aac910103a4cafb921ecb41916268d
ACR-9089cf8208a94942958bb86ad98af3d5
ACR-ae4e40310f4d4b8f9f7b4a0cef7a645a
ACR-5dd3256307944ead91efd6fe60bd4e53
ACR-1f748f6eb19f4e2aa5ef58c60d746c93
ACR-2479f2927ab14b8db8ee642eb7cca109
ACR-eeb581de413c42b9953422b321bd3ef3
ACR-1afb7d76758248f1888ec6ce11e6960d
ACR-c86dab6e42f943f7aa89d005685b647a
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.Plugin;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.groups.Tuple.tuple;

class PluginInstancesLoaderTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  PluginInstancesLoader loader = new PluginInstancesLoader(new PluginClassloaderFactory());

  @AfterEach
  void closeLoader() throws IOException {
    loader.close();
  }

  @Test
  void instantiate_plugin_entry_point() {
    var def = new PluginClassLoaderDef("fake");
    def.addMainClass("fake", FakePlugin.class.getName());

    var instances = loader.instantiatePluginClasses(Map.of(def, getClass().getClassLoader()));
    assertThat(instances).containsOnlyKeys("fake");
    assertThat(instances.get("fake")).isInstanceOf(FakePlugin.class);
  }

  @Test
  void plugin_entry_point_must_be_no_arg_public() {
    var def = new PluginClassLoaderDef("fake");
    def.addMainClass("fake", IncorrectPlugin.class.getName());

    loader.instantiatePluginClasses(Map.of(def, getClass().getClassLoader()));

    assertThat(logTester.logs(LogOutput.Level.ERROR))
      .contains("Fail to instantiate class [org.sonarsource.sonarlint.core.plugin.commons.loading.PluginInstancesLoaderTests$IncorrectPlugin] of plugin [fake]");
  }

  @Test
  void define_classloader(@TempDir Path tmp) throws IOException {
    var jarFile = tmp.resolve("fakePlugin.jar").toFile();
    Files.createFile(jarFile.toPath());
    var info = new PluginInfo("foo")
      .setJarFile(jarFile)
      .setMainClass("org.foo.FooPlugin")
      .setMinimalSqVersion(Version.create("5.2"));

    var defs = loader.defineClassloaders(Map.of("foo", info));

    assertThat(defs).hasSize(1);
    var def = defs.iterator().next();
    assertThat(def.getBasePluginKey()).isEqualTo("foo");
    assertThat(def.getFiles()).containsExactly(jarFile);
    assertThat(def.getMainClassesByPluginKey()).containsOnly(MapEntry.entry("foo", "org.foo.FooPlugin"));
    //ACR-0ffea5d6ec264b61af7e309e5efd8d78
  }

  @Test
  void extract_dependencies() {
    var jarFile = getFile("sonar-checkstyle-plugin-2.8.jar");
    var info = new PluginInfo("checkstyle")
      .setJarFile(jarFile)
      .setMainClass("org.foo.FooPlugin")
      .setDependencies(List.of("META-INF/lib/commons-cli-1.0.jar", "META-INF/lib/checkstyle-5.1.jar", "META-INF/lib/antlr-2.7.6.jar"));

    var defs = loader.defineClassloaders(Map.of("checkstyle", info));

    assertThat(defs).hasSize(1);
    var def = defs.iterator().next();
    assertThat(def.getBasePluginKey()).isEqualTo("checkstyle");
    assertThat(def.getFiles()).hasSize(4);
    assertThat(def.getFiles()).extracting(File::getName, f -> {
      try {
        return DigestUtils.md5Hex(Files.readAllBytes(f.toPath()));
      } catch (IOException e) {
        return e.getMessage();
      }
    }).containsExactlyInAnyOrder(
      tuple("sonar-checkstyle-plugin-2.8.jar", "e7e5e17e5e297ac88d08122c56d72eb7"),
      tuple("commons-cli-1.0.jar", "d784fa8b6d98d27699781bd9a7cf19f0"),
      tuple("checkstyle-5.1.jar", "d784fa8b6d98d27699781bd9a7cf19f0"),
      tuple("antlr-2.7.6.jar", "d784fa8b6d98d27699781bd9a7cf19f0"));
  }

  /*ACR-b344f180f8ad43a0bd67e5d74889acd9
ACR-70e847bc2fa047849c36fe52b9b73f0b
   */
  @Test
  void test_plugins_sharing_the_same_classloader(@TempDir Path tmp) throws IOException {
    var baseJarFile = tmp.resolve("fakeBasePlugin.jar").toFile();
    baseJarFile.createNewFile();
    var extensionJar1 = tmp.resolve("fakePlugin1.jar").toFile();
    extensionJar1.createNewFile();
    var extensionJar2 = tmp.resolve("fakePlugin2.jar").toFile();
    extensionJar2.createNewFile();
    var base = new PluginInfo("foo")
      .setJarFile(baseJarFile)
      .setMainClass("org.foo.FooPlugin");

    var extension1 = new PluginInfo("fooExtension1")
      .setJarFile(extensionJar1)
      .setMainClass("org.foo.Extension1Plugin")
      .setBasePlugin("foo");

    var extension2 = new PluginInfo("fooExtension2")
      .setJarFile(extensionJar2)
      .setMainClass("org.foo.Extension2Plugin")
      .setBasePlugin("foo");

    var defs = loader.defineClassloaders(Map.of(
      base.getKey(), base, extension1.getKey(), extension1, extension2.getKey(), extension2));

    assertThat(defs).hasSize(1);
    var def = defs.iterator().next();
    assertThat(def.getBasePluginKey()).isEqualTo("foo");
    assertThat(def.getFiles()).containsOnly(baseJarFile, extensionJar1, extensionJar2);
    assertThat(def.getMainClassesByPluginKey()).containsOnly(
      entry("foo", "org.foo.FooPlugin"),
      entry("fooExtension1", "org.foo.Extension1Plugin"),
      entry("fooExtension2", "org.foo.Extension2Plugin"));
    //ACR-563f32e9843644c290345ac25750d0e6
  }

  //ACR-f3754d0d06bc4a2eb27eded6b334ef4f
  @Test
  void skip_plugins_when_base_plugin_missing(@TempDir Path tmp) throws IOException {
    var extensionJar1 = tmp.resolve("fakePlugin1.jar").toFile();
    extensionJar1.createNewFile();
    var extensionJar2 = tmp.resolve("fakePlugin2.jar").toFile();
    extensionJar2.createNewFile();

    var extension1 = new PluginInfo("fooExtension1")
      .setJarFile(extensionJar1)
      .setMainClass("org.foo.Extension1Plugin");
    var extension2 = new PluginInfo("fooExtension2")
      .setJarFile(extensionJar2)
      .setMainClass("org.foo.Extension2Plugin")
      .setBasePlugin("foo");

    var defs = loader.defineClassloaders(Map.of(
      extension1.getKey(), extension1, extension2.getKey(), extension2));

    assertThat(defs).hasSize(1);
    var def = defs.iterator().next();
    assertThat(def.getFiles()).containsOnly(extensionJar1);
    assertThat(def.getMainClassesByPluginKey()).containsOnly(
      entry("fooExtension1", "org.foo.Extension1Plugin"));
  }

  //ACR-99df3e2e12744e638cc5ae85dc8d1329
  @Test
  void should_be_able_to_delete_jar_after_unload() throws IOException {
    var jarFile = PluginClassloaderFactoryTests.testPluginJar("classloader-leak-plugin/target/classloader-leak-plugin-0.1-SNAPSHOT.jar");

    var tmpCopy = Files.createTempFile("leak-plugin", ".jar");
    Files.copy(jarFile.toPath(), tmpCopy, StandardCopyOption.REPLACE_EXISTING);

    var info = new PluginInfo("leak")
      .setJarFile(tmpCopy.toFile())
      .setMainClass("org.sonar.plugins.leak.LeakPlugin");

    var instances = loader.instantiatePluginClasses(List.of(info));
    var instance = instances.get("leak");

    //ACR-40f8ae60f3944394830a200b7d98fe76
    instance.define(null);

    loader.close();

    Files.delete(tmpCopy);
  }

  public static class FakePlugin implements Plugin {
    @Override
    public void define(Context context) {
      //ACR-c879603c0b8f472c93208e0330934a00
    }
  }

  /*ACR-d0b143b7fda04d70baf9f90b35a4b632
ACR-4720192811454236b6eef146eb246787
   */
  public static class IncorrectPlugin implements Plugin {
    public IncorrectPlugin(String s) {
    }

    @Override
    public void define(Context context) {
      //ACR-9d49565f5f6141538b581b9fcb5e0077
    }
  }

  private File getFile(String filename) {
    return FileUtils.toFile(getClass().getResource("/" + filename));
  }
}
