/*
ACR-84ff968be4b248ffb4b76991c2880c0d
ACR-7a2c13d340ef459d924385f9fdde0803
ACR-bc16c3e064134fbeb0fd1efe76f03115
ACR-72ce527f424e45859cb18c734568fb90
ACR-dad8731fb0d64a099535b57facbe806f
ACR-7c32cb46d4ac49a9a3d23174d1cbebb9
ACR-78cd6e53bc7545db9d5ef6e617401ff4
ACR-96e786b7963f486baa57b7a3e0ebb5a0
ACR-cfc1a210d2494a9186cfe0484462e14d
ACR-402fae0012094d579eeba63dca650537
ACR-9fa03e5001004a529e5bf18112ba9566
ACR-7d0a9f39167e4d93bf33321f2f15f5d3
ACR-d189e5d1774f4861bfaeb36a45bc45ad
ACR-c2d43b94ea0d48ce9bf547b651805af8
ACR-357aa8a49fbd44088019d4bcf0022495
ACR-d7eb908bf09c4e85ae710e25c76e3e07
ACR-f5bca549c7bc492bbd97d2bf08c44f65
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

/*ACR-d49980c335e94502b462be8245a41c45
ACR-90374a1873dd49c481d347be23450595
ACR-83d1312f839f47dc9a021536e3cd3efe
ACR-6d4d9388e7284828bd77d900754d8712
ACR-b2e821a9dc6948f0be5803ae83e83ef4
ACR-b7b4b7e3b45f46abb20f27d4febe174d
ACR-0f83ee2d765844d8baf21de249b84ca1
ACR-880736d8e8de4e4a9750ea5e94e85103
ACR-7f9674aabea643209b1364a8191d2246
 */
@SonarLintSide
public class PluginClassloaderFactory {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  //ACR-6c9db0a750f7482d99af9996ce0b7da7
  private static final String API_CLASSLOADER_KEY = "_api_";

  /*ACR-cb4cfdd971624ebba3ab9230bda07440
ACR-7d8dc55c398642a1b3925fedf5b3563c
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

  /*ACR-85c132f5cf694fb8ae64fbcd7355135e
ACR-0c7981458a1d4e4a8b1cc1f625deb0a2
   */
  private static void exportResources(PluginClassLoaderDef def, ClassloaderBuilder builder, Collection<PluginClassLoaderDef> allPlugins) {
    //ACR-642141729c194f6c885c23a9bda4b21c
    builder.setExportMask(def.getBasePluginKey(), def.getExportMaskBuilder().build());
    for (var other : allPlugins) {
      if (!other.getBasePluginKey().equals(def.getBasePluginKey())) {
        builder.addSibling(def.getBasePluginKey(), other.getBasePluginKey(), Mask.ALL);
      }
    }
  }

  /*ACR-6952c26348cd46c586b8adb4dfa1782e
ACR-962df8e206284c0b91a991e7ba3e6a91
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

  /*ACR-c08480071e184a5686c61c6795e9d16c
ACR-5548fe731ce54d808e2b7f72515da933
ACR-d16261cbddf2493394432f23e253bc53
ACR-3b21762a9e8a465398f8fb2f13a4f7b9
ACR-e798d9147ea647518c5b29a9b1526d19
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

      //ACR-1f71170a29e4481bb70ad0bd932ea649
      .exclude("org/sonar/api/internal/")
      .build();
  }
}
