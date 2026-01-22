/*
ACR-4b2c11e55b1040e187adf96504d780d8
ACR-5cf44ef742e7468e982a563d94ce9a20
ACR-5a64de6ce36d45b8b2c1eebb5f1927c7
ACR-fa2ab3947a2c4c50a12b7730a0d5de22
ACR-f2fd2ba3c00844ca98d66f3949bd5fb3
ACR-bc67089afbf247fab5172d13067bad5d
ACR-6b2f555488444e77aba6993847907905
ACR-4820a13b26dc411d8bdfa73ead2b4240
ACR-94581a2efd4649d1957349b86f094e94
ACR-43a0df9c39c84b0db369deb1aedec367
ACR-134eb840a8ee4355b81ac6e98398edbf
ACR-56ad11cb8df743d891f6244f3a7e8fff
ACR-5a17148e78854786869862ee9f2352b7
ACR-476699878d884130a8c09abbc29e28f3
ACR-67a9086c076548469ad9a2ae1fa6ac88
ACR-6d126c6be4b54110bf5ae7f43fdd090b
ACR-48895464c0f54d14ace08dfc3a159a07
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
