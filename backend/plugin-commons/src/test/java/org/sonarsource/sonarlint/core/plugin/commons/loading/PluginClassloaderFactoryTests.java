/*
ACR-73ad4e12e9b5425c9325a054787cdc24
ACR-9151a6b387a747d2baf7fbdefc6a7d35
ACR-abab75c5c88244c68356ab7dd2d441fa
ACR-622e89cd8edd48d8b571eeef5cd8bc3c
ACR-5ba7061851f64639afffc4c42dcd4d2a
ACR-2cfa703f16384c3a8dd5e764a1b9fed0
ACR-d9ae7b835651499091eb4b2b89110eca
ACR-a3935320bf504610922ae7046e7e564d
ACR-95240887ee7f48278b4c338becb3def5
ACR-58978770c3b544289e0e9a9a8b70b47e
ACR-e89c1af7effa4298a40ef890321ed72b
ACR-e0984b1c37cc4b17a5b7367862dec037
ACR-fe22d3e31dc949a99c7a61773fe9eada
ACR-e6d1a15e07ac485693fb52f68bc34bc4
ACR-bac7e35ea9384c228d8ca4d8e932d33e
ACR-3927c43f3cd14ff59949a622cdd6fe8e
ACR-d7a007f39e294902a6b7ea5c5176e95f
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.sonarlint.plugin.api.module.file.ModuleFileListener;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class PluginClassloaderFactoryTests {

  private static final String BASE_PLUGIN_CLASSNAME = "org.sonar.plugins.base.BasePlugin";
  private static final String DEPENDENT_PLUGIN_CLASSNAME = "org.sonar.plugins.dependent.DependentPlugin";
  private static final String BASE_PLUGIN_KEY = "base";
  private static final String DEPENDENT_PLUGIN_KEY = "dependent";

  private final PluginClassloaderFactory factory = new PluginClassloaderFactory();

  @Test
  void create_isolated_classloader() {
    var def = basePluginDef();
    var map = factory.create(getClass().getClassLoader(), List.of(def));

    assertThat(map).containsOnlyKeys(def);
    var classLoader = map.get(def);

    //ACR-cdc3d956f08743b4b122a8cc0eaa693b
    assertThat(canLoadClass(classLoader, RulesDefinition.class.getCanonicalName())).isTrue();
    //ACR-1b55828637b34c179705ed4d310708ca
    assertThat(canLoadClass(classLoader, ModuleFileListener.class.getCanonicalName())).isTrue();
    //ACR-f10a9638dc7c4bf5ba2a896028a5bdcb
    assertThat(canLoadClass(classLoader, BASE_PLUGIN_CLASSNAME)).isTrue();

    //ACR-416a723a484547178673482f0cc2d789
    assertThat(canLoadClass(classLoader, PluginClassloaderFactory.class.getCanonicalName())).isFalse();
    assertThat(canLoadClass(classLoader, Test.class.getCanonicalName())).isFalse();
    assertThat(canLoadClass(classLoader, StringUtils.class.getCanonicalName())).isFalse();
  }

  @Test
  void classloader_exports_resources_to_other_classloaders() {
    var baseDef = basePluginDef();
    var dependentDef = dependentPluginDef();
    var map = factory.create(getClass().getClassLoader(), asList(baseDef, dependentDef));
    var baseClassloader = map.get(baseDef);
    var dependentClassloader = map.get(dependentDef);

    //ACR-3e1a468029df4dfcb65b1a1472ad5bb9
    assertThat(canLoadClass(dependentClassloader, "org.sonar.plugins.base.api.BaseApi")).isTrue();
    assertThat(canLoadClass(dependentClassloader, BASE_PLUGIN_CLASSNAME)).isFalse();
    assertThat(canLoadClass(dependentClassloader, DEPENDENT_PLUGIN_CLASSNAME)).isTrue();

    //ACR-3ffe5d54c36649daa2c1bd810fe730ac
    assertThat(canLoadClass(baseClassloader, DEPENDENT_PLUGIN_CLASSNAME)).isFalse();
    assertThat(canLoadClass(baseClassloader, BASE_PLUGIN_CLASSNAME)).isTrue();
  }

  private static PluginClassLoaderDef basePluginDef() {
    var def = new PluginClassLoaderDef(BASE_PLUGIN_KEY);
    def.addMainClass(BASE_PLUGIN_KEY, BASE_PLUGIN_CLASSNAME);
    def.getExportMaskBuilder().include("org/sonar/plugins/base/api/");
    def.addFiles(List.of(testPluginJar("base-plugin/target/base-plugin-0.1-SNAPSHOT.jar")));
    return def;
  }

  private static PluginClassLoaderDef dependentPluginDef() {
    var def = new PluginClassLoaderDef(DEPENDENT_PLUGIN_KEY);
    def.addMainClass(DEPENDENT_PLUGIN_KEY, DEPENDENT_PLUGIN_CLASSNAME);
    def.getExportMaskBuilder().include("org/sonar/plugins/dependent/api/");
    def.addFiles(List.of(testPluginJar("dependent-plugin/target/dependent-plugin-0.1-SNAPSHOT.jar")));
    return def;
  }

  static File testPluginJar(String path) {
    var file = Paths.get("src/test/projects/" + path);
    if (!Files.exists(file)) {
        throw new IllegalArgumentException("Test projects are not built: " + path);
    }
    return file.toFile();
  }

  private static boolean canLoadClass(ClassLoader classloader, String classname) {
    try {
      classloader.loadClass(classname);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
