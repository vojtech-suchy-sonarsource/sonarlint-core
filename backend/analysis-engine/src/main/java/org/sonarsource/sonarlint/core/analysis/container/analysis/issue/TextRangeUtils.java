/*
ACR-b197f5267d4444028a3ee2112b212f37
ACR-26e3d17bb9554e96a6844c619bb7916d
ACR-0727c331db124cfeaff8e58cade02a27
ACR-d1e69d93cf8140f1833a9552e3faeb3b
ACR-bf6401459e4f4b289a32abd0f018f8bf
ACR-8bc0c1527dbc4225b0ee25fc41d9f4e9
ACR-cc46a62c841e4552a18c39f7ee77d673
ACR-82ef33206805436a92727b6559915f77
ACR-a29040ffd7104ceebafc23ab56120fb7
ACR-0d428d7cecf04570aa77d8bd418ca07e
ACR-eea1baec16b3424bbced0008b4a98f36
ACR-904d76e72ff1456eb8f02ea822211fbf
ACR-230b08c8515146f7b1b0135e1dd0321c
ACR-8ad70fc46c1242088795daf0d59bf081
ACR-92c24fbef5d545f9a7575387f7114691
ACR-8d90433456044c9fb5517cea4365dfdb
ACR-0fc209ce00cb440bb3997c7677e2df0c
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
