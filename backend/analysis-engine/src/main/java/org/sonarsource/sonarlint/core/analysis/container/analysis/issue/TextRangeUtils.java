/*
ACR-46d2b05122544c10a96f678a6bc903b5
ACR-4feede3c2be64e419f3cc059126f9b1f
ACR-a32f173c301a4dc0a48e30d4c069df57
ACR-73d73a9c78544b17bd14bdc003c26896
ACR-44179c67efae48eabc95caf429fe47fe
ACR-d4a9475c0d2f47e78a3e86df9ef8a47b
ACR-de3048919f224747a5b9624a81553ec2
ACR-0b0afc8b1c184f159e338d00b011d430
ACR-ba9c8b70eea74a2ba62f82612e1b897a
ACR-b7ef9b1a4c4d433fa3be2196260ba740
ACR-7c8b00510bbc4107b6034dc012a6a831
ACR-1e459e458c914c49b6d6a75a4f3e5163
ACR-32ca6dd610d248ca9832e855849e4638
ACR-3565348949f64929b5563340e29e239e
ACR-0785fbc9bce7489da28a23c3cb945649
ACR-1a8673f480174cf9ad5f0ae469d481d4
ACR-2dec6c749f8d47d5a98a08fd536d8038
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue;

import org.sonarsource.sonarlint.core.commons.api.TextRange;

public class TextRangeUtils {

  private TextRangeUtils() {
  }

  public static TextRange convert(org.sonar.api.batch.fs.TextRange analyzerTextRange) {
    return new TextRange(
      analyzerTextRange.start().line(),
      analyzerTextRange.start().lineOffset(),
      analyzerTextRange.end().line(),
      analyzerTextRange.end().lineOffset());
  }

}
