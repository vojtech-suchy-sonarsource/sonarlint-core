/*
ACR-0bf2b8e22cdb4b37a3f15df70f257d64
ACR-d8acf153168f4fcf9016a5fb135d7184
ACR-fff12a6782574fad8340d123432a4d78
ACR-b7389ef987794740868b84935ca6d7e7
ACR-51d39f412b874989aa7165c3fe929274
ACR-0a507432cd9047b2a1a21699a7d50c91
ACR-56358ac5bd654f4eaf2ad07578ad0d49
ACR-58a78166ed6e47c9a96434fc214da39f
ACR-b26a5e978cd64f6e93f32b51b5d55862
ACR-27ce91e2b0b2416e8ec3cf8ca027d729
ACR-f0a561f3da464f5887dff35e3bb7e2ae
ACR-7b8254b5f0b34553af4fac148f3e46dd
ACR-0014288bd5be455fac395bb7b5c0fcde
ACR-6dd62379c13745bdbc781c92533da734
ACR-d6510a97db3e44b496929339c6e1f05a
ACR-e2ef3083c14d492e85952ec2ddbb448a
ACR-996d3000dbea4cc5a382b31a60e3ce7e
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

  /*ACR-e0eba4f9488e46a99980e3fd1e6e6079
ACR-bdd67e532c954f9c8d0d0e7f0b25028a
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
