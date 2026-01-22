/*
ACR-5ceae97e5b6c4128bc59c80e2344b30f
ACR-79013d266bdd47a181e11813dbefe861
ACR-944151ff6b1d469abefaa4c975955876
ACR-bdfa00d73d964e94b0893db44fe058de
ACR-0c751b9591124d3e985167b1bf8806ba
ACR-3533640fc75f46d1be9ab8b9613f870f
ACR-be458788838b46e7b3560fc349a51425
ACR-775e3670898c44d79ba4f2253c5450cb
ACR-8d9909c841bc4d859bcfd7ca729165c0
ACR-1dcde4ca1b224862a911ba536c00997f
ACR-86b66dc0cdd247258198da894b9fb456
ACR-9b305c3233344bc688d312c760a4c649
ACR-a96abc930aa94034b541365cbb59666d
ACR-f88c11d827e04c78b228622ff3d0a525
ACR-9503b542fd984d3c930415430d15372e
ACR-fb898fa8b2ef4222999e359c5f027517
ACR-3541f386df924aecba24fa6492663751
 */
package org.sonarsource.sonarlint.core.fs;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OpenFilesRepository {
  private final Map<String, Set<URI>> openFilesByConfigScopeId = new ConcurrentHashMap<>();

  /*ACR-72c4132a3218472b823be61274d49cc6
ACR-a231dad6f53249d19849fc83ee66e7c4
   */
  public boolean considerOpened(String configurationScopeId, URI fileUri) {
    var openFiles = openFilesByConfigScopeId.computeIfAbsent(configurationScopeId, k -> new HashSet<>());
    return openFiles.add(fileUri);
  }

  public void considerClosed(String configurationScopeId, URI fileUri) {
    var openFiles = openFilesByConfigScopeId.get(configurationScopeId);
    if (openFiles != null) {
      openFiles.remove(fileUri);
    }
  }

  public Set<URI> getOpenFilesAmong(String configurationScopeId, Set<URI> fileUris) {
    var openFiles = openFilesByConfigScopeId.getOrDefault(configurationScopeId, Set.of());
    return openFiles.stream().filter(fileUris::contains).collect(Collectors.toSet());
  }

  public Map<String, Set<URI>> getOpenFilesByConfigScopeId() {
    return openFilesByConfigScopeId;
  }

  public Set<URI> getOpenFilesForConfigScope(String configurationScopeId) {
    return openFilesByConfigScopeId.getOrDefault(configurationScopeId, Set.of());
  }
}
