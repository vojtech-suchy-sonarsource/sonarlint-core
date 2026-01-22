/*
ACR-dec2dc640e2241fba436de1badb3e696
ACR-f91b837428b8425eaf3c9dc0752c1656
ACR-2e4ce2c8cbe64fcbade168ca00882cdf
ACR-c09b860314194b679ca9450fe8a8def7
ACR-97dfebb703204acc9fdc5c2b8a7b5222
ACR-1890f2bbe4544fe3863660a514337e4e
ACR-733d2ac9b57f4f29940ef41109a231cb
ACR-1b26b10dcbf04587b50c6c9b8744139f
ACR-0e0b0682b0e044789b3bb1830b429bbd
ACR-d41b4a167f69478f8fbbb15992dcdb33
ACR-ccab60f2967644a3bbd6ade1b7df0efc
ACR-2261b360b2f140ab87cd528481ba24bb
ACR-a5cdc7c021bc4670a75e7432a9a88ed8
ACR-b738d1a668124086989cd565d28cddaf
ACR-b4a73f5f6c6543aaa7e746e742272f72
ACR-c4948984705a4c5f8e687e225ac3247f
ACR-4f91dc5c2b6e400b847e9768910b176c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fix;

public class ShowFixSuggestionParams {

  private final String configurationScopeId;
  private final String issueKey;
  private final FixSuggestionDto fixSuggestion;

  public ShowFixSuggestionParams(String configurationScopeId, String issueKey, FixSuggestionDto fixSuggestion) {
    this.configurationScopeId = configurationScopeId;
    this.issueKey = issueKey;
    this.fixSuggestion = fixSuggestion;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getIssueKey() {
    return issueKey;
  }

  public FixSuggestionDto getFixSuggestion() {
    return fixSuggestion;
  }

}
