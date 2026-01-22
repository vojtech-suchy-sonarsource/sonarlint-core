/*
ACR-ecc8d1d19945430b976393a6d4bb569f
ACR-55e3db269b7f40f2be0f310d2a0559cf
ACR-56b484da66cf41f6809a8fef724d29d1
ACR-aa320ff48d104d3ebb64c3d475036c37
ACR-7bb57a4a477a4980afc956d665412224
ACR-5fde5a3594b643d69f9f83072a9accc1
ACR-f9be18edadba4f1aa7b07d21aca50f8d
ACR-7eae1f2cde284fe8acfa8d87a749b490
ACR-67cb25aa73014ed78030e44a5364b667
ACR-b007fc70526f41f5ac658f8ca941810a
ACR-e7e54cc9c0f8483da1baa6452885a600
ACR-8e0f2441848d475b8ce47b9a6fc6ee1a
ACR-edf78b7186c945809a846ee29c234982
ACR-5c898f0bde124be8b3c4a98c2e1b1a7b
ACR-c4741d5e305b48edb65547632b1422ee
ACR-782c1253900f4182b134d6c003ab6505
ACR-28980c19793e4c73bf89958921775b07
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class StorageUtils {

  public static Set<SonarLanguage> deserializeLanguages(Optional<String> lastEnabledLanguages) {
    Set<String> lastIssueEnabledLanguagesStringSet = Collections.emptySet();
    Set<SonarLanguage> lastIssueEnabledLanguagesSet = new HashSet<>();

    if (lastEnabledLanguages.isPresent()) {
      lastIssueEnabledLanguagesStringSet = Stream.of(lastEnabledLanguages.get().split(",", -1))
        .collect(Collectors.toSet());
    }

    for(String languageString : lastIssueEnabledLanguagesStringSet){
      var language = SonarLanguage.getLanguageByLanguageKey(languageString);
      if(language.isPresent()){
        lastIssueEnabledLanguagesSet.add(language.get());
      }
    }

    return lastIssueEnabledLanguagesSet;
  }

}
