/*
ACR-6e88fd8eb7f04a8c9d31e13cb6be7e62
ACR-ea27e0993ac94975b4094cb1169fbe0e
ACR-d238571666ec4c4f9c1f47a9da0f17ca
ACR-592771c538d44e0bbddac0cac7f5c02d
ACR-25f06488b3f847029fe295a87721b4d3
ACR-f3c1b0b995ae42f884323428243ea186
ACR-d56f075c60ef40a196235f215649f331
ACR-5ff150be3a0c41f686b70ebc7b720893
ACR-5d8cda73db15434ca2b638fd94689fce
ACR-de49c910e3a84df5bef27cc5cc570411
ACR-d640814af758420fad75ff7b37ac913c
ACR-c71710388207495da162fa9f07e598c4
ACR-529c6def2f2f49ec9eec35ff52bb31a4
ACR-fb1e84c28b84420a92b27eb84721919f
ACR-2cb41e6362e149858ed044d7db937c72
ACR-f8a92fb8bc304b60b13f3db301d1e793
ACR-c80acf4979114975932de769f56a79ff
 */
package org.sonarsource.sonarlint.core.analysis.api;

import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

public interface WithTextRange {

  /*ACR-40749504a6944f2f9409081608745793
ACR-40ed6d1ca786459fb2ac94ced948ad5b
   */
  @CheckForNull
  TextRange getTextRange();

  @CheckForNull
  default Integer getStartLine() {
    var textRange = getTextRange();
    return textRange != null ? textRange.getStartLine() : null;
  }

  @CheckForNull
  default Integer getStartLineOffset() {
    var textRange = getTextRange();
    return textRange != null ? textRange.getStartLineOffset() : null;
  }

  @CheckForNull
  default Integer getEndLine() {
    var textRange = getTextRange();
    return textRange != null ? textRange.getEndLine() : null;
  }

  @CheckForNull
  default Integer getEndLineOffset() {
    var textRange = getTextRange();
    return textRange != null ? textRange.getEndLineOffset() : null;
  }

  static TextRange convert(org.sonar.api.batch.fs.TextRange analyzerTextRange) {
    return new TextRange(
      analyzerTextRange.start().line(),
      analyzerTextRange.start().lineOffset(),
      analyzerTextRange.end().line(),
      analyzerTextRange.end().lineOffset());
  }
}
