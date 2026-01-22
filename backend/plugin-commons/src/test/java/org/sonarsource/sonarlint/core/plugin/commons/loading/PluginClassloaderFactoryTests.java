/*
ACR-80788a07e98449d6a9b3821600f15cfa
ACR-e1d5c789ec3d41be963a15093153de56
ACR-21494e18b67244eda1591e012492cfa6
ACR-d582872f87114fdc814ca4e3a31acbf5
ACR-8eae272b2f00489b9025441b2a01c995
ACR-e954f6382a3443acbaeb8eaa31472059
ACR-22257adf8a03474aa63c7afe287ffb99
ACR-efbd570a686d4145b97561b192a26829
ACR-7361ea7f80814ef4a27f142bfedbf06b
ACR-03cb14b5d8054753aa2590a33c221f81
ACR-d26dcf2181f248c5aaa72566bddcebab
ACR-e18fc6e882844030a66060619a77e7d2
ACR-0dab2a1004b54a9192c7ab3878dd386f
ACR-f578483238314bafbfd8f454085d08f1
ACR-337c0daf5a8a4a86a4b8133e994fe3d8
ACR-c0d7f72d6edc4089a21e86f404074181
ACR-06456afe2d904022afacd699d487777a
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

    //ACR-812acaa054024d918ed3514326f319b6
    assertThat(canLoadClass(classLoader, RulesDefinition.class.getCanonicalName())).isTrue();
    //ACR-97572daf381640ffa78c96ef59a83836
    assertThat(canLoadClass(classLoader, ModuleFileListener.class.getCanonicalName())).isTrue();
    //ACR-4eb05724a90e4b6890cfc76571825f3b
    assertThat(canLoadClass(classLoader, BASE_PLUGIN_CLASSNAME)).isTrue();

    //ACR-686bd40b05ee484b8b412d2c8c4de378
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

    //ACR-ad00ca2090104b4dbe17e77f9f9009d4
    assertThat(canLoadClass(dependentClassloader, "org.sonar.plugins.base.api.BaseApi")).isTrue();
    assertThat(canLoadClass(dependentClassloader, BASE_PLUGIN_CLASSNAME)).isFalse();
    assertThat(canLoadClass(dependentClassloader, DEPENDENT_PLUGIN_CLASSNAME)).isTrue();

    //ACR-4d6e4f74f11646fab6d44907cc0245d5
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
