/*
ACR-4792e6afbc2e4c12834087cae86f6897
ACR-89b464b1a2d04dacaa07ce5391786a2a
ACR-87e6919a150644379c4b40a4055dc3ce
ACR-7492c5be613540fb9ba12b05e43eb774
ACR-c07a81ada35840fcab26417d3c4f80fd
ACR-4517c3a03fee481eb8cb1f8d3906254c
ACR-a4dff949192e4678a35af39ddf4a0728
ACR-dcd3705424974b25881e357db976d7f2
ACR-e1797a35808b41a1a0c5f241a1edde82
ACR-2beb293d0b774ce49763e727640260c8
ACR-c58b5d90d6bd40f6a959bebc56cb6f5e
ACR-63434336a08f47ce8629fd10665a8865
ACR-b084d4a807374dfb89c7e991b5d2a87c
ACR-da90e8155c564bbb8a378bd542cbd970
ACR-432fbc1b175947bc9a389a1e37c55808
ACR-85ae8fee09c247638432c2a50f10a658
ACR-e9fe7d969f4a4dac9c88e5a03bd5a684
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.KnownFinding;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

public class KnownIssueMatchingAttributesMapper implements MatchingAttributesMapper<KnownFinding> {

  @Override
  public String getRuleKey(KnownFinding issue) {
    return issue.getRuleKey();
  }

  @Override
  public Optional<Integer> getLine(KnownFinding issue) {
    return Optional.ofNullable(issue.getLineWithHash()).map(LineWithHash::getNumber);
  }

  @Override
  public Optional<String> getTextRangeHash(KnownFinding issue) {
    return Optional.ofNullable(issue.getTextRangeWithHash()).map(TextRangeWithHash::getHash);
  }

  @Override
  public Optional<String> getLineHash(KnownFinding issue) {
    return Optional.ofNullable(issue.getLineWithHash()).map(LineWithHash::getHash);
  }

  @Override
  public String getMessage(KnownFinding issue) {
    return issue.getMessage();
  }

  @Override
  public Optional<String> getServerIssueKey(KnownFinding issue) {
    return Optional.empty();
  }
}
