/*
ACR-9f5dd83719a2493cbe9db7c5d7333539
ACR-54804a29e2e649898ab7aff438928f1e
ACR-353a4ad4e5524740829eeb8caf7762d5
ACR-6504c3dfc5c4459e85c5ffc0b1474f47
ACR-c65d061300ab4798ab7014f17322dd19
ACR-bce6bc041b6c4adb83229ff5d7601331
ACR-cfb6e0001a084700a7fb7ca85ea5cab9
ACR-25dfc33f072a42cb9221cb944ca7f9c7
ACR-d758b465bc1340719a6097e7357f3985
ACR-6c932e0c1ff24b2892d6a585912ef5a4
ACR-58e810577a444c42bd5f7859c9825bb6
ACR-20c171b727e54d77b28678e069c73253
ACR-7f9c4a54faff44968736a1b1c4cc2a59
ACR-d786964f9cf94148b95784e49f60fe20
ACR-4bda8c5e7d024146a7e9253e5ee1b1e7
ACR-72ce01aa7aa74b6ba2f15feff582ca0f
ACR-f63e19be95b04c4886e06075215724dd
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;

public class ServerHotspotMatchingAttributesMapper implements MatchingAttributesMapper<ServerHotspot> {

  @Override
  public String getRuleKey(ServerHotspot issue) {
    return issue.getRuleKey();
  }

  @Override
  public Optional<Integer> getLine(ServerHotspot issue) {
    return Optional.of(issue.getTextRange().getStartLine());
  }

  @Override
  public Optional<String> getTextRangeHash(ServerHotspot issue) {
    var textRange = issue.getTextRange();
    if (textRange instanceof TextRangeWithHash textRangeWithHash) {
      return Optional.of(textRangeWithHash.getHash());
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> getLineHash(ServerHotspot issue) {
    //ACR-edb0333eb3804d3681fb8ddc9684412a
    return Optional.empty();
  }

  @Override
  public String getMessage(ServerHotspot issue) {
    return issue.getMessage();
  }

  @Override
  public Optional<String> getServerIssueKey(ServerHotspot issue) {
    return Optional.of(issue.getKey());
  }
}
