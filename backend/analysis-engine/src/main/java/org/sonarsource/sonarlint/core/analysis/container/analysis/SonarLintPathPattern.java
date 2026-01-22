/*
ACR-262f5d51657d4dd7bc6f553d88a520b1
ACR-34ed2ed158474220beaf377776348462
ACR-137595f57c5048bd98df64028f449c5c
ACR-a7775483a5df4d07997489ab826f93c5
ACR-fb6c938127244941a806ec158235c343
ACR-0a558e40c59046e1ac0f23a955a96172
ACR-ea0955f0ebb64343bbe753d2fcda9771
ACR-647f86ca44f74619a9e8bd01ce7cd2ae
ACR-0a8e68f53ef141be90f8924be2d38dc4
ACR-602888309fc1443484acccd6caede881
ACR-58d8fe62b05d458f9b25b96b5d2135fd
ACR-11ae54cf610448ff9d26af959cb74b6f
ACR-90ad8a9360504527afe45ba72197e201
ACR-5364832873e5403bbd035bba5fc68e86
ACR-22982944cc3c4b8bbbf7f6ae22a374f0
ACR-99b5e5b590494488adaf1af9dc9b21dd
ACR-cda32cc051e345aeb103cc1219675552
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

/*ACR-71b1523be3e243cb858c283c3b30646d
ACR-2a7ea988b3fe483da7db32f2d24f679a
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
