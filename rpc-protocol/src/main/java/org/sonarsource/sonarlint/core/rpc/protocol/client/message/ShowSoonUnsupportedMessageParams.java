/*
ACR-09225a6f7bb34b5d95536390d8141f3b
ACR-23991c96ba524d7f802f8e53fffb5c57
ACR-82f4ba06d4844fe5ae2d7b10301cbf35
ACR-3169852f48234ca19eb11daf3b2700a8
ACR-f67003877600421498b3f7b1da816122
ACR-c946888205a648e88b08d2f1e4815e6f
ACR-5c2bb952da8942a49f33367256ab6cd5
ACR-c7b19b99456943a3945a028f6d439799
ACR-ed1e0763188f4ed1b41626a6e9bd68fd
ACR-c9759d26cdc24d499b29cb5400ec3457
ACR-ed3f50e091e24287bcdef686176cda04
ACR-b81acb210a5941cfa975e7089de9bb8e
ACR-a750e82674e74e71a64393062f37cd35
ACR-b755150b662542e68799952a324de00d
ACR-a4a7ae0a55a5450f97058117459a1224
ACR-d998ffa261f149d78cdd5fc539d48e3b
ACR-b88004df24134f02b73a7b358cad7c07
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

/*ACR-fbdf2ce2b3a44bbb86db7a1cbb1825ae
ACR-be980cf2ef48446ba0228418878ec2b9
ACR-d5059b15e8774a1a92dc99bb2d464729
ACR-eaedfb6d6a0546348782e6c9c86f3589
ACR-35c6c628e96341b2a881bf8c615a8295
ACR-64f96798011f4626919eb8f3bfb17449
ACR-71eb8f202f8747b28bde4d61b2d92972
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
