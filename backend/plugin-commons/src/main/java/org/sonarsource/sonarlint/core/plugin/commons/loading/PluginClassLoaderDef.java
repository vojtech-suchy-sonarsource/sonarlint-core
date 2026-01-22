/*
ACR-b2d640bec8af4470987f55d0d10fbf66
ACR-a975d160ab6348a8b840cd53a1d6499e
ACR-0c0c154c4dcd4eac9e7ad4b5a7b57239
ACR-8c334980bf504613beb5e2729ce36098
ACR-e4389019b46d40538fb8ba0344081e59
ACR-dc180d9a7da541f19fef5eaff9321040
ACR-a12277b3ddf940d3acdc3e98c5be490c
ACR-c878354644ee4b7e85ae04bfa05001e8
ACR-1175345e3aa24f8e94a728dea6ce483e
ACR-5a82cd9934e14aecada3bd4522f18862
ACR-2280438c153c43a4927f5f26d585a1c8
ACR-d569a3ef265445faa28a5c1fd7062f97
ACR-e0a6111145c7411da1c887748fdef22b
ACR-702049cfc62a40feadb56288065b4f6a
ACR-27ce1c0bb3be452ca05965b6c748163c
ACR-bca6010cb4cd421a9b333ff83ee66e62
ACR-b486bcf9fe9f42c0ad9240683d4a0d6d
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonar.classloader.Mask;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/*ACR-587f5a765881485f9d8dcce1aca3583a
ACR-1208d917ca9b4755acb55d4fc71c9917
 */
class PluginClassLoaderDef {

  private final String basePluginKey;
  private final Map<String, String> mainClassesByPluginKey = new HashMap<>();
  private final List<File> files = new ArrayList<>();
  private final Mask.Builder mask = Mask.builder();

  PluginClassLoaderDef(String basePluginKey) {
    this.basePluginKey = basePluginKey;
  }

  String getBasePluginKey() {
    return basePluginKey;
  }

  List<File> getFiles() {
    return files;
  }

  void addFiles(Collection<File> f) {
    this.files.addAll(f);
  }

  Mask.Builder getExportMaskBuilder() {
    return mask;
  }

  Map<String, String> getMainClassesByPluginKey() {
    return mainClassesByPluginKey;
  }

  void addMainClass(String pluginKey, @Nullable String mainClass) {
    if (isNotEmpty(mainClass)) {
      mainClassesByPluginKey.put(pluginKey, mainClass);
    }
  }

}
