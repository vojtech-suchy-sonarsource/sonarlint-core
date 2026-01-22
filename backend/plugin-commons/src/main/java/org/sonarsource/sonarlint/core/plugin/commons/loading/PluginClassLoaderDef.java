/*
ACR-3fe99f7e960246559c22d5f07b8b4cca
ACR-833d33b26b764a52b5cb644aede38b80
ACR-b0162c069ed1403d8334873362a3a65b
ACR-193a2faebef3495ab00fce3675245007
ACR-c08f587c04ac41498be01a0a39ebf6ef
ACR-789cd1a0d2964d9ea3e8a8603d6f0594
ACR-0c7cd7531be144f8a2a77771d9b78d61
ACR-4f1119572c694b16a8c463d9b1febbad
ACR-1a300161520048b2922287366a1b4580
ACR-c58531fad80a474d9c27e2333e7d076d
ACR-6bb55174a9e047bc8b1a323b589fb6e2
ACR-ea9e48ab3d564efa8118fd563e942a7e
ACR-2ed958e6d2ca4c5e9c2f83bc502e9a57
ACR-c2ad2d2ef61e4a55afd1421d3c922b04
ACR-2bf9d39c94fe45ca8a29246581dcc02b
ACR-22555b3765d64e25a07f809d20889b75
ACR-f9d8da123bb343668e3d3312351f4c00
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

/*ACR-1ac14dd6fb7f4122bea21b6254cb4d40
ACR-2863ebdbb0974730995d09fbae0f836e
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
