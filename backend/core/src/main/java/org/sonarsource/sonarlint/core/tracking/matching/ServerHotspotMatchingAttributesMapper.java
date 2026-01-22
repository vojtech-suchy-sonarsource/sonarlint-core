/*
ACR-b71b536aff084ae6a7aa8774fb6d8b99
ACR-557af2029f8c409ba5957e0a24cd11df
ACR-d9e38c5d51f246828c6b387774e9eda6
ACR-4e4aea4a268240e09e4e9917f69d09e2
ACR-cf947aa3f11b48d5a18653611e237c6a
ACR-24166da2f112493d93b977df2a3dd62e
ACR-af20edf43b4249eb8b1c4912a37cfcc5
ACR-6fbd20de4db24c7489ab0eb8b808eab3
ACR-70516230a02c486fa25373b7320ae707
ACR-003b890185914b2a96227d80e0fca494
ACR-72f4d1d116b443dfaf4291c1ab9b19eb
ACR-e609f817706747f9b2099e80a426a39e
ACR-e87e0e729cae42489414ef39c7289746
ACR-188815d98e854f30befaa5a41d3fd569
ACR-2192a499907e40a7a519481447591e99
ACR-081485082fe74bedac73b5498fdc1a76
ACR-d064ff23a27f47a9a4d15ecb8fe62055
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
    //ACR-3b010ae0febe4af183a2a0640376ebda
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
