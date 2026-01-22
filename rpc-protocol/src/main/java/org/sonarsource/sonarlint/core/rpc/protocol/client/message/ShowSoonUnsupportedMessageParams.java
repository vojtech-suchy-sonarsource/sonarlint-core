/*
ACR-aa6c18c03fa34c28b9da10b58d3d67fc
ACR-75c37ff20fdb42c5b1dd76747f17ad30
ACR-f79bd55d77674b6bbeb7e78c1f13f606
ACR-d4d46de953914517a38ef9ed39ac7ffb
ACR-443efaa7a3664c7cbfc96c6815065291
ACR-86537eb68a9b4a7187a16ec9a483b210
ACR-51b081c166d449e397bca94970e967d0
ACR-7d955d40bfaa4e6cb19ae112245b9911
ACR-a857b36431bf451dbf0a415a016572ab
ACR-eb8e1054dd0145859a70e5b96b0e09d0
ACR-359bd37b947e4684912674b8fb752d46
ACR-1f2debeafc7a4786891892d8efeecaee
ACR-e6f739c0676d4343a5ede2bfabf13672
ACR-bc84e0dfbb284a35acaa80b647201112
ACR-cdae142914c4462199c5e5ccf3a6744d
ACR-fc278a72d24b471198800f65aa37f666
ACR-d1007e70d6c642eb83ab20595bfc4e94
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

/*ACR-072d142cead14163a333dc413783c732
ACR-a03a9e181c7c4e3f9c782ea68eba01ac
ACR-a7f24db2e2144ec69a9f08194e3ff25e
ACR-9eb566e8bcd248439e1b3c98b03eb911
ACR-41ccbfd838e54e09a77b74849fdb986a
ACR-52d74aa3c7cb4be58dee459fc14f0c35
ACR-253778d027c14fa8b8722b98c78950b9
 */
public class ShowSoonUnsupportedMessageParams {

  private final String doNotShowAgainId;
  private final String configurationScopeId;
  private final String text;

  public ShowSoonUnsupportedMessageParams(String doNotShowAgainId, String configurationScopeId, String text) {
    this.doNotShowAgainId = doNotShowAgainId;
    this.configurationScopeId = configurationScopeId;
    this.text = text;
  }

  public String getDoNotShowAgainId() {
    return doNotShowAgainId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getText() {
    return text;
  }
}
