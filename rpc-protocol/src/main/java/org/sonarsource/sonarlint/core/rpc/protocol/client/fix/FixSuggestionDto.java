/*
ACR-34b5e06478ac44b8a560d27224874c53
ACR-b3643e7bfef843b19f3747f0d7a64b20
ACR-b782559ea00c466da8a189d3976d7d27
ACR-37ca39139cd34b31873d71cb4549d60f
ACR-197aa0921b5645438aa9661f21794b39
ACR-7f396aa430be482e8ebdafdc621d58b8
ACR-621c73da2e2b443485f575b26b7c14fa
ACR-9138ab6b1c1c4887b742e80af46253b4
ACR-ee15a73db0704eb6a2c4222aa7edd14a
ACR-f4cb59327efd4cea9504ed2a6b527e70
ACR-42cfa4dbdd3146b58a6c3bb55bd3a1b8
ACR-f03b7ba711f646d59e209f3eb092c06f
ACR-6a515819c960409681a628c8f0f1db5b
ACR-b149831b8dcd480a90951e2af49ee97c
ACR-6e0aea4375864024a05db1390ca2b642
ACR-dfbd3cbe37e84336a8734b8578916cd8
ACR-93a934ebf6bd44a39227c831b97f90d6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fix;

public class FixSuggestionDto {

  private final String suggestionId;
  private final String explanation;
  private final FileEditDto fileEdit;

  public FixSuggestionDto(String suggestionId, String explanation, FileEditDto fileEdit) {
    this.suggestionId = suggestionId;
    this.explanation = explanation;
    this.fileEdit = fileEdit;
  }

  public String suggestionId() {
    return suggestionId;
  }

  public String explanation() {
    return explanation;
  }

  public FileEditDto fileEdit() {
    return fileEdit;
  }

}
