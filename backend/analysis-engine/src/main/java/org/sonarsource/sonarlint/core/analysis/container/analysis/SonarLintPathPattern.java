/*
ACR-3c8aa5ddbb104771a39c772bb0bcecae
ACR-d72f03ec08cc4fd4b14ac0d667f29b20
ACR-84848aaecd484a11907ce1d53bd5256c
ACR-6a794262bb904aa281dac2b4b370bfde
ACR-3a4f9e42d7f041e08dc5e40bd037588f
ACR-65de3c29e2224a9b8b4dbd87f8700811
ACR-3a907647e2234e5c940bc16c7250b226
ACR-0aaaf190e74c40e3b2df7233959571bd
ACR-b71646e04ceb4f819fbab7850c3650cc
ACR-69d03ece51e04beb983983c23dbf06b8
ACR-f89d10dbcb6e496ab5e4b849e34b6711
ACR-89891cf32f3743539433edb65a95d9ef
ACR-f9ccdbcb7f074ae29681a7b327f845b0
ACR-55c999ed8ef342a89e39bea036a340b9
ACR-324f27a5d0b4470ab91861d94d61d646
ACR-ae008357eb01494fa5eeb7ade56b3a14
ACR-07bb731c144a4adca47f0df46fd5c44c
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.utils.PathUtils;
import org.sonar.api.utils.WildcardPattern;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-25f77075f1324a34a39d8cb76df7b37a
ACR-b2f483d01ac54e8fa7b8181483cc8b45
 */
public class SonarLintPathPattern {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  final WildcardPattern pattern;

  public SonarLintPathPattern(String pattern) {
    if (pattern.startsWith("file:")) {
      LOG.warn("Unsupported path pattern: " + pattern);
      pattern = pattern.replaceAll("^file:/*", "");
    }
    if (!pattern.startsWith("**/")) {
      pattern = "**/" + pattern;
    }
    this.pattern = WildcardPattern.create(pattern);
  }

  public static SonarLintPathPattern[] create(String[] s) {
    var result = new SonarLintPathPattern[s.length];
    for (var i = 0; i < s.length; i++) {
      result[i] = new SonarLintPathPattern(s[i]);
    }
    return result;
  }

  public boolean match(InputFile inputFile) {
    return match(inputFile.relativePath(), true);
  }

  public boolean match(String filePath) {
    return match(filePath, true);
  }

  public boolean match(InputFile inputFile, boolean caseSensitiveFileExtension) {
    return match(inputFile.relativePath(), caseSensitiveFileExtension);
  }

  public boolean match(String filePath, boolean caseSensitiveFileExtension) {
    var path = PathUtils.sanitize(filePath);
    if (!caseSensitiveFileExtension) {
      var extension = sanitizeExtension(FilenameUtils.getExtension(path));
      if (StringUtils.isNotBlank(extension)) {
        path = Strings.CI.removeEnd(path, extension);
        path = path + extension;
      }
    }
    return path != null && pattern.match(path);
  }

  @Override
  public String toString() {
    return pattern.toString();
  }

  static String sanitizeExtension(@Nullable String suffix) {
    return StringUtils.lowerCase(Strings.CS.removeStart(suffix, "."));
  }
}
