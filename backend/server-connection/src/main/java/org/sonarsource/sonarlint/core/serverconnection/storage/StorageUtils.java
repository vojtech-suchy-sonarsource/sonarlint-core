/*
ACR-025e32efc3934039987c32b41adf971c
ACR-27aca06ff13b4e87a4f27a520c27555d
ACR-7e99106304eb4ac48f1c874cc7ac36c3
ACR-e394260c9f7b457c8f0346a43194fc78
ACR-e8b44f9551344765b6ebb0d4e59d5f30
ACR-d314cf6ac2d147f7a072b99a80efafca
ACR-1d36e77bb5804eb8a38be83f57910e21
ACR-b6dcf8b5a6e14b0b997791d4cf0737cd
ACR-e601f1fdb81d4758991cb9c2080094d5
ACR-1ac9a5e1f242443a9d554324f43048ad
ACR-8f3d5654d5dd4a19900fe58560ace2bb
ACR-2cb5c8fa429a4520bce19defed490340
ACR-0dda701034fc45fab1f7fde23a65a4b8
ACR-411157bd7c5f4edda831f42cf0d09002
ACR-2303328957f344eeb9e89e33d3d0ca2f
ACR-3945a257abc0472aa73423f00495d095
ACR-1bc3bb1c9225438488225eb625f32f8a
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
