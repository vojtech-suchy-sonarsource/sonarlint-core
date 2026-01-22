/*
ACR-d4ae1fc4e18d44d383a4f32133123bfe
ACR-4967a4e853e84aeaa5ee998ab0ad04ee
ACR-b964f646409245219d7bc776e98382f3
ACR-20283e7d94f640db8ebd9851ea0b7cda
ACR-ed727977490c4a788a813dc0cb884e17
ACR-00599e964bfd4169b4176850f25a5e09
ACR-e26e8eafbe074ff7a9338a1496df5126
ACR-acfefdcc46904f0a8f9a441b9ce61577
ACR-e1a4e1ec88804e98afe2673f39b1eb2e
ACR-1e3ce93723824b8099d2936473213b3d
ACR-f2ceadf5f83e42ecbb342421d649f541
ACR-e9d48eddc2204b4ea1641a268d0de049
ACR-2fbfe33ba153480baa49aa449e3edd55
ACR-ca35fba40b644443ae10572c9c7f28a3
ACR-e8ef6fa5d2004e4ea6ec5db09fc531ef
ACR-8a1c1e25fa894ad19508dd8afeae0d8f
ACR-bbd223e837e349bc8211e8134a88c11f
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.net.URI;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.CheckForNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.MessageException;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-7c347e82cfc2461ca7efa565cbc1e654
ACR-910eff84d0e64245b44d5de071885573
 */
public class LanguageDetection {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  /*ACR-2fe65022c74a4fa39502c501c6171713
ACR-8f2803816d7b449f950e35063da33279
   */
  private final Map<SonarLanguage, String[]> extensionsByLanguage = new LinkedHashMap<>();

  public LanguageDetection(Configuration config) {
    for (SonarLanguage language : SonarLanguage.values()) {
      var extensions = config.get(language.getFileSuffixesPropKey()).isPresent() ? config.getStringArray(language.getFileSuffixesPropKey())
        : language.getDefaultFileSuffixes();
      for (var i = 0; i < extensions.length; i++) {
        var suffix = extensions[i];
        extensions[i] = sanitizeExtension(suffix);
      }
      extensionsByLanguage.put(language, extensions);
    }
  }

  @CheckForNull
  public SonarLanguage language(InputFile inputFile) {
    return detectLanguage(inputFile.filename(), inputFile.uri());
  }

  private SonarLanguage detectLanguage(String fileName, URI fileUri) {
    SonarLanguage detectedLanguage = null;
    for (Entry<SonarLanguage, String[]> languagePatterns : extensionsByLanguage.entrySet()) {
      if (isCandidateForLanguage(fileName, languagePatterns.getValue())) {
        if (detectedLanguage == null) {
          detectedLanguage = languagePatterns.getKey();
        } else {
          //ACR-3cb793d0d9194772a9257174536e3016
          throw MessageException.of(MessageFormat.format("Language of file \"{0}\" can not be decided as the file extension matches both {1} and {2}",
            fileUri, getDetails(detectedLanguage), getDetails(languagePatterns.getKey())));
        }
      }
    }
    if (detectedLanguage != null) {
      LOG.debug("Language of file \"{}\" is detected to be \"{}\"", fileUri, detectedLanguage);
      return detectedLanguage;
    }
    return null;
  }

  private static boolean isCandidateForLanguage(String fileName, String[] extensions) {
    for (String extension : extensions) {
      if (fileName.toLowerCase(Locale.ENGLISH).endsWith("." + extension)) {
        return true;
      }
    }
    return false;
  }

  private String getDetails(SonarLanguage detectedLanguage) {
    return detectedLanguage + ": " + String.join(",", extensionsByLanguage.get(detectedLanguage));
  }

  public static String sanitizeExtension(String suffix) {
    return StringUtils.lowerCase(Strings.CS.removeStart(suffix, "."));
  }

}
