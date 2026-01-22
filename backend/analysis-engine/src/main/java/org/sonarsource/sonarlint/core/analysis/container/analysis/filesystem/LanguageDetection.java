/*
ACR-d5c87d67b258496aae21e1812c33caf0
ACR-1edfb6a20d8d4f9bbff1644d25b829b5
ACR-1feec37f0a49424a92b44668d2665cd6
ACR-2ea4737703a143a58a799a379ad57999
ACR-b679fa5660f34cdaad39f45685b82e4d
ACR-2e0b96595111495c81a45edbe2011d96
ACR-1c3df1cb10174294aede6f0e9b15624a
ACR-2fe50d0367c04b45bd709cb06a89a49c
ACR-d6fc09dec79749f0823daf99dd6ed3ee
ACR-8e8d182f22464695b1f82d4c9f68bdc0
ACR-647256a64b464a99be6a34e8eef9ae5f
ACR-4783f54448e34b918f6f66ac71f52f78
ACR-162cc1af31bd48648a3445a8005a0ef3
ACR-d833b9ff90914663bf2d7b2dcad43df6
ACR-540aae01cd9a49a1afa0569da74fa857
ACR-e4b96e5367bc47caa229721597d6a021
ACR-03530b58e0244135adafc364fb4afc93
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

/*ACR-a6f6be620bbc43da9b3f8f3ff0de455a
ACR-905ca1fde5e0485195decff172927e78
 */
public class LanguageDetection {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  /*ACR-6d021db18fc1415283a0ce0fea5ca5a0
ACR-834e31812d7843faa9229e862314112d
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
          //ACR-ea971472bfb04f4886e104d27c2162c2
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
