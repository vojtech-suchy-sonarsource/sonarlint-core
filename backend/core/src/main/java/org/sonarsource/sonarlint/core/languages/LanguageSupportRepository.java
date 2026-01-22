/*
ACR-afd956a2c0974051bdeb74de187ab8e1
ACR-b7620c8d17694e9fb77e312353390f7c
ACR-07dbb5f6eec84103a263059c0b906adf
ACR-2c8227cb7bbc4044ad4137b0061cb6f9
ACR-70d0d3efb2084038958e4afe9c8e6caf
ACR-e609353852974d5f9184091ed303bc32
ACR-da8a60404a894925991151e9ce226423
ACR-56f4542c987d4aefb7557c34427b1944
ACR-9bc8be3e1d8243fca0a1de8d234a7f0f
ACR-e6bad62639a148d8af08343dd911d44d
ACR-195af88f64f847cb9c6c9e0ca2a932c7
ACR-a0d2b2999d874f0ebc264f37421f7f22
ACR-17b2ee42977749c28bc9821f5778afe7
ACR-724db7542f18458897756febadecc0a0
ACR-95e83d3ae7cc4bc28086a2f8a38b3a6c
ACR-4334e8814e804a8e984ddbfccb832393
ACR-81ea409156424b81b50254935a293955
 */
package org.sonarsource.sonarlint.core.languages;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class LanguageSupportRepository {
  private static final EnumSet<SonarLanguage> LANGUAGES_RAISING_TAINT_VULNERABILITIES =
    EnumSet.of(SonarLanguage.CS, SonarLanguage.JAVA, SonarLanguage.JS, SonarLanguage.TS, SonarLanguage.PHP, SonarLanguage.PYTHON);
  private final EnumSet<SonarLanguage> enabledLanguagesInStandaloneMode;
  private final EnumSet<SonarLanguage> enabledLanguagesInConnectedMode;

  public LanguageSupportRepository(InitializeParams params) {
    this.enabledLanguagesInStandaloneMode = toEnumSet(
      adaptLanguage(params.getEnabledLanguagesInStandaloneMode()), SonarLanguage.class);
    this.enabledLanguagesInConnectedMode = EnumSet.copyOf(this.enabledLanguagesInStandaloneMode);
    this.enabledLanguagesInConnectedMode.addAll(adaptLanguage(params.getExtraEnabledLanguagesInConnectedMode()));
  }

  @NotNull
  private static List<SonarLanguage> adaptLanguage(Set<Language> languagesDto) {
    return languagesDto.stream().map(e -> SonarLanguage.valueOf(e.name())).toList();
  }

  private static <T extends Enum<T>> EnumSet<T> toEnumSet(Collection<T> collection, Class<T> clazz) {
    return collection.isEmpty() ? EnumSet.noneOf(clazz) : EnumSet.copyOf(collection);
  }

  public Set<SonarLanguage> getEnabledLanguagesInStandaloneMode() {
    return enabledLanguagesInStandaloneMode;
  }

  public Set<SonarLanguage> getEnabledLanguagesInConnectedMode() {
    return enabledLanguagesInConnectedMode;
  }

  public boolean areTaintVulnerabilitiesSupported() {
    var intersection = EnumSet.copyOf(LANGUAGES_RAISING_TAINT_VULNERABILITIES);
    intersection.retainAll(enabledLanguagesInConnectedMode);
    return !intersection.isEmpty();
  }
}
