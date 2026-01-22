/*
ACR-fb14c30568e64c9ea151d24d3555700f
ACR-648a189996914478a2b2b209475d97bb
ACR-6f8ba2b8659a4ed6ab689af3d23a6627
ACR-31be60dcb0934d7b830dfaded284ff77
ACR-15cb3f4052994426ac0d0bb78e89e4c6
ACR-38ba19721a7a4bf3b5cd5908c2132659
ACR-d2a3930441a048fa8a4d703e3bf4cea6
ACR-0e394faeadc04b1ab235acb65f953431
ACR-61fb8b5c2a064e3abfe8556800a33700
ACR-06b1807423b14f4ab0887f425ee45aeb
ACR-10268876df7b4da8b03a5ad841b238e4
ACR-7d5fe183ef8b4424a26fde67764d41ac
ACR-1b718c0b36b745e38619d18cfc2ea383
ACR-6b713fa0aadc47ae88655eb0811eadfa
ACR-a1302307fa1247b6b1b3a174478b03c4
ACR-6040eb05589041c3a652d4e2498bb288
ACR-c0c550d4360a456d8549bf3ceb3ddbe3
 */
package org.sonarsource.sonarlint.core.analysis.api;

import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

public interface WithTextRange {

  /*ACR-10d69f215de24cf48c3b142cb1e3036a
ACR-1006999ba69d4790838e49cbdf54c467
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
