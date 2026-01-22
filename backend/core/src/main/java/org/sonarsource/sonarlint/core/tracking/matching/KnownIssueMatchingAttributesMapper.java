/*
ACR-77f54a94518242fd94e6e3880d43e064
ACR-9261354997aa4105b14156f2e500ed55
ACR-b619f341515c4e4995cb65109966e604
ACR-63767f2430234ef38bb1832483a3ef3f
ACR-95abd6124b53412bb26fa465fbb1bd39
ACR-dad836a2050d4f848d9f79ccada793b3
ACR-90c325986ce749c097d8fbb4017624c4
ACR-3fad4844d5a64430a963063f13d01e52
ACR-f45675077bb44d2981f3f1343577b6ca
ACR-4f906ce4512e4a78b7712313ff470261
ACR-1019c7a277af4655ababff0e317c9743
ACR-2a2216e693354cddb751d6721e0e6244
ACR-cc53798af05b43938922cd078a77f7d7
ACR-79174ff290bf45c3b6244e90f7f6e561
ACR-e5a194c46aec423497ddd5e6e3aa54c7
ACR-27035271b296401ba3aebde287883416
ACR-a844b9fcf56d4c8f8228434ceffaeb49
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
