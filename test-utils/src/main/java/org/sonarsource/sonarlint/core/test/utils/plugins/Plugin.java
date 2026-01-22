/*
ACR-926f7bfcb4ae43ac91f2d2af2a9cc6df
ACR-4ad8504c20fa4c5c8dc6730ba49c9183
ACR-34fde4119bd948cab52c96fb0042ac59
ACR-8c44782a6fae4c8cb5d1feafbb0ff66c
ACR-f86f60b3d90d4a80b5c636b304957aaa
ACR-5413c96d0e6b4a67b788abe19dc38277
ACR-e0a1df05768e4cd691f8a836a3b21c70
ACR-4097de0dcc244e428fff3a8ad9bcb916
ACR-43efe239517b4f30bb41aef6b8fce32b
ACR-77fafa101eeb4d3281fdeb4f4c1c94b9
ACR-20a6c8bebe104269b95299c16cb1e2f6
ACR-031ab51f3fe94196a3068c4f4390e5ed
ACR-7d4d401df1a44a7fa4219469d5236c7a
ACR-318b241f2e1f475a90f647db909b4b05
ACR-ac12e8e520864d4593deeaf7a84744f6
ACR-cadec2bcd48246368d3e6883c9a39850
ACR-a13ead2027b240a38d4667b1871cfd28
 */
package org.sonarsource.sonarlint.core.test.utils.plugins;

import java.nio.file.Path;
import java.util.Set;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class Plugin {
  private final String key;
  private final Set<Language> languages;
  private final Path path;
  private final String version;
  private final String hash;

  private static String getPluginKeyFromLanguage(Language language) {
    return SonarLanguage.valueOf(language.name()).getPluginKey();
  }

  public Plugin(Language language, Path path, String version, String hash) {
    this(Set.of(language), path, version, hash);
  }

  public Plugin(String key, Language language, Path path, String version, String hash) {
    this(key, Set.of(language), path, version, hash);
  }

  public Plugin(Set<Language> languages, Path path, String version, String hash) {
    this(getPluginKeyFromLanguage(languages.iterator().next()), languages, path, version, hash);
  }

  public Plugin(String key, Set<Language> languages, Path path, String version, String hash) {
    this.key = key;
    this.languages = languages;
    this.path = path;
    this.version = version;
    this.hash = hash;
  }

  public Set<Language> getLanguages() {
    return languages;
  }

  public String getPluginKey() {
    return key;
  }

  public Path getPath() {
    return path;
  }

  public String getVersion() {
    return version;
  }

  public String getHash() {
    return hash;
  }
}
