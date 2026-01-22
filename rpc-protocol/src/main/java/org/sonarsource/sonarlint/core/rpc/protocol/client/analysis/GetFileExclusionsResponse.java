/*
ACR-ff046f0f92764ee9bd8591eebccccdbc
ACR-a05e62137aea4fa1813a358928f86b52
ACR-465001dc39f448c3b1c47e4d5ddf73b7
ACR-b341ff1092f44c379524de5022aab705
ACR-ed048cf6da35419dbd31faf6fc62410c
ACR-c78d55990e29463ea37243624b169063
ACR-4911440a8bcd4e989fadde4a6aa5b085
ACR-77ba6180b19a401d8e35d370b4090835
ACR-86b79e5da23b4a16a3ac0388447629ca
ACR-9299a47c9202400795dc0056b271c37e
ACR-3be213818c8949939d01b57c2037e817
ACR-4642c5ba43284b32b817f10d4e7abd08
ACR-a0496f9034664dc9aea512646839dac0
ACR-59f52170116a40fea227d7f07f5d2163
ACR-fb1522f5aa334775984d99230fdd1d3e
ACR-5df77da54bf34f6c9bc395b456136494
ACR-af5fbaf4424d45d8a319f588ac9823cf
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.util.Set;

public class GetFileExclusionsResponse {
  private final Set<String> fileExclusionPatterns;

  public GetFileExclusionsResponse(Set<String> fileExclusionPatterns) {
    this.fileExclusionPatterns = fileExclusionPatterns;
  }

  public Set<String> getFileExclusionPatterns() {
    return fileExclusionPatterns;
  }
}
