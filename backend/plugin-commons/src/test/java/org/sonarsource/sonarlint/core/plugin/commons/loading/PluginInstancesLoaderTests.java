/*
ACR-0047f773a82f4c84a87d8c4e22e6e4c2
ACR-c971b0fda1c746c9b53e3506ab1f6595
ACR-1bb6cafabd164559a8df55ab594fdb5b
ACR-3bc02527fb7d4894af61d0953dbfff10
ACR-8a445b5abd724602af2e1cd65edcf172
ACR-3774e4ba4676465f98baad86251b04ed
ACR-7561372366f544f0b8690a175ef83b72
ACR-92eb29c6c7fa471ebc165726580606d1
ACR-8dbf33908c684532a73b7dae9a528cb3
ACR-52fb2fbaa66549ae9953c9abfa9a63b7
ACR-dbf0199011b84cc2891fdae7eb9c7b2f
ACR-a63c32a34fe34a78af9abc3bf76f552d
ACR-487bcf0c4824402399b852fda3f97a6f
ACR-cdea73e0065f4031b81a77c8e4883738
ACR-eb2725d8447e4143bd8b2f1907d0d50e
ACR-dfec9d15b6ae40cfa75e6f318d55697e
ACR-204b85d0388a4433a8ff44620f42ed60
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
    //ACR-d9b80155de174c4184363aa241c2aad1
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

  /*ACR-99616e39edeb43569126f4f4407afd88
ACR-830acd7221ec4cfca0e4c53a19bd02ff
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
    //ACR-26e4dda84d14437dbd334f7fe2909260
  }

  //ACR-bb07d2dd79784db0bb23207ff0cfbf55
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

  //ACR-627ddb3fe5b740258038386ecf792671
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

    //ACR-1b6ac7ebc4ab4ec28eb0965de4431f04
    instance.define(null);

    loader.close();

    Files.delete(tmpCopy);
  }

  public static class FakePlugin implements Plugin {
    @Override
    public void define(Context context) {
      //ACR-2c191adbbd9c4b00940f6cdb50e68cab
    }
  }

  /*ACR-4eba1a98d74b4ca9a6b801abebb7de54
ACR-9810abebb30b4ac2832e3a4ae25123ad
   */
  public static class IncorrectPlugin implements Plugin {
    public IncorrectPlugin(String s) {
    }

    @Override
    public void define(Context context) {
      //ACR-4f86af528309439b9d1325626fff973a
    }
  }

  private File getFile(String filename) {
    return FileUtils.toFile(getClass().getResource("/" + filename));
  }
}
