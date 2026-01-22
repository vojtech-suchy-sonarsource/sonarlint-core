/*
ACR-d51f57ce80994c5382f67601117cba16
ACR-2dcd64f3baea4c8eb826a62328f600e2
ACR-97544769cbe04557ad1a1c7ea9dbbddc
ACR-06e43b3e35134945bdbd1804ae49aa07
ACR-e0207a981e1e42d4a1d37ce06176c323
ACR-8cfb6c5c07a94f32a7868f33d0e96062
ACR-036bbbe9b96846b3b268e5c4324b2a83
ACR-0f197df690f94f5282126b42a9d9d22f
ACR-4222261e32c643a1a221327442a6cb29
ACR-53c0cd76bcd24599904fc9031a0da32a
ACR-56548e77aec04f6b8687c2a9c6ce6ac5
ACR-5c9a75b82f6941ea8873f49e4a9628b9
ACR-9611daa6cae945238f03949a0eaa6f24
ACR-efbd0a5ce6c94db88affd98a1b8c9452
ACR-f841eed3d9484df0a584686ac0b4344a
ACR-63d105235f6a41a98f88933b8c8d35e4
ACR-0c7a04ea5e9948cc819c4774a7332cef
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import org.sonar.classloader.ClassloaderBuilder;
import org.sonar.classloader.Mask;
import org.sonarsource.api.sonarlint.SonarLintSide;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

import static org.sonar.classloader.ClassloaderBuilder.LoadingOrder.PARENT_FIRST;

/*ACR-8d17fb6aef9e45dfb9f3772d175b796e
ACR-bcda0271ee3e4cb2845239d792c67849
ACR-24483535469d4b088029d71f601ac6e7
ACR-a3300b62597a472c8a98f2363c9982d5
ACR-d7c0df7a03204240a4a53ac00f5cb87e
ACR-a7fb30a7a7874b06848b59d5b41218de
ACR-5c9e50d3c0fb438cb7a6da3b0efe04b3
ACR-3774c8e934a647f497b21b48c891aa96
ACR-fe8d86a52fb74a2287eedb16f10102c6
 */
@SonarLintSide
public class PluginClassloaderFactory {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  //ACR-cce222e7b4974f9e8990c434086cd4df
  private static final String API_CLASSLOADER_KEY = "_api_";

  /*ACR-03b54a75a4a14535869aa7e5045dde86
ACR-ba3746669aad4d968ee55e4a5ad06f21
   */
  Map<PluginClassLoaderDef, ClassLoader> create(ClassLoader baseClassLoader, Collection<PluginClassLoaderDef> defs) {
    var builder = new ClassloaderBuilder();
    builder.newClassloader(API_CLASSLOADER_KEY, baseClassLoader);
    builder.setMask(API_CLASSLOADER_KEY, apiMask());

    for (var def : defs) {
      builder.newClassloader(def.getBasePluginKey());
      builder.setParent(def.getBasePluginKey(), API_CLASSLOADER_KEY, Mask.ALL);
      builder.setLoadingOrder(def.getBasePluginKey(), PARENT_FIRST);
      for (var jar : def.getFiles()) {
        builder.addURL(def.getBasePluginKey(), fileToUrl(jar));
      }
      exportResources(def, builder, defs);
    }

    return build(defs, builder);
  }

  /*ACR-89b4d30759674cac908dfef7e16d2322
ACR-1fede8c6f4174685b40c315f443bdf71
   */
  private static void exportResources(PluginClassLoaderDef def, ClassloaderBuilder builder, Collection<PluginClassLoaderDef> allPlugins) {
    //ACR-123214ccace34420bd37a68acabd1955
    builder.setExportMask(def.getBasePluginKey(), def.getExportMaskBuilder().build());
    for (var other : allPlugins) {
      if (!other.getBasePluginKey().equals(def.getBasePluginKey())) {
        builder.addSibling(def.getBasePluginKey(), other.getBasePluginKey(), Mask.ALL);
      }
    }
  }

  /*ACR-5dbde20a962f419c9b7d28ea2bae4b02
ACR-c74fa5dc1f40455f93aa283a6f01612c
   */
  private static Map<PluginClassLoaderDef, ClassLoader> build(Collection<PluginClassLoaderDef> defs, ClassloaderBuilder builder) {
    Map<PluginClassLoaderDef, ClassLoader> result = new IdentityHashMap<>(defs.size());
    var classloadersByBasePluginKey = builder.build();
    for (var def : defs) {
      var classloader = classloadersByBasePluginKey.get(def.getBasePluginKey());
      if (classloader == null) {
        LOG.error("Fail to create classloader for plugin '{}'", def.getBasePluginKey());
      } else {
        result.put(def, classloader);
      }
    }
    return result;
  }

  private static URL fileToUrl(File file) {
    try {
      return file.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /*ACR-8fd5d304f85a4fa0adf26f7a660112d5
ACR-197ba87d4c364fd9a59b02eb5a3b24c9
ACR-79c4ac394b81449ca3dead4f86b2ea99
ACR-662b1b7c7d32417d8351bcd64c2db717
ACR-1cc81f606f1e47a68896ea1b1a3b31b4
   */
  private static Mask apiMask() {
    return Mask.builder()
      .include("org/sonar/api/")
      .include("org/sonarsource/api/sonarlint/")
      .include("org/sonar/check/")
      .include("net/sourceforge/pmd/")
      .include("com/sonarsource/plugins/license/api/")
      .include("org/sonarsource/sonarlint/plugin/api/")
      .include("org/slf4j/")

      //ACR-da600c19f4904c00aaf2ea93155587e9
      .exclude("org/sonar/api/internal/")
      .build();
  }
}
