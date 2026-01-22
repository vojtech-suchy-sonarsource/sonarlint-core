/*
ACR-d1b46b93f036400ea31b556b2bfd6679
ACR-0f5d038f85644865919a60c89c3d21bd
ACR-4a9df958e889436da3349c9c8c8f7e26
ACR-ca2f39d636a74a7f8dd232a2869e1a32
ACR-1d455bf0088a422198e3420e6d02ea54
ACR-e068063aece64b0fac628a82999f9778
ACR-348b0778f08849d99d1abcf79a528680
ACR-1ad5386d71fa46a0868005dcb3884418
ACR-e84c19d248644c288545d25947d47a07
ACR-0061a3d7d0ef4c02b328878a36ac89fa
ACR-cc526155a127416297c2683fd4d55f88
ACR-fcebd329010940a2975590a1dbe0e214
ACR-2323c637e5f14b5aa7baf37974b46ac7
ACR-6cee68513d8241c99d4aefbfaba81031
ACR-a1e08c765a494702a2c913880a07240b
ACR-15dbef7616ff4cd99ef1243291386d32
ACR-5247836ff65245e29ae9c82d043e1fc2
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
