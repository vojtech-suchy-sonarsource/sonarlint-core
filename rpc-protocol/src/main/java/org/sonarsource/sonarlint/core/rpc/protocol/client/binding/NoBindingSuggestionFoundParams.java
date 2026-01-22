/*
ACR-209e686b66b449bdb5ed15001ea44b51
ACR-179a9cdef3364e47a652b506f9917543
ACR-2feae1a5b0c849cab7e43259e8e73477
ACR-a07595f1d7974de8952d3edff8493277
ACR-408b9580f5914514b48ead04ff4fdae9
ACR-a9983d43d66f4513a45428c56c7a3a7e
ACR-09c695c9d331488ba8a57cc91713fa82
ACR-70d90a4ee1a04e16a400864397f70c83
ACR-8d16236e2aa146ea88217165bb3672cf
ACR-af9a6c670cba4349a3ac117fa99cd140
ACR-bdf0366391a94bf28c96a55e0bc54e21
ACR-674ef20cc4404d89abf1674029149b7c
ACR-79d34eed64664ee5b00f19227762fb18
ACR-75483ad1c6574f739bcb2b0f28e4b509
ACR-e428dfde41904127b99df5cbb77a0b64
ACR-473d49210b0c4315be03ec24c315b044
ACR-40df966b8be5492e8be8a22796b9337d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.binding;

public class NoBindingSuggestionFoundParams {

  private final String projectKey;
  private final boolean isSonarCloud;

  public NoBindingSuggestionFoundParams(String projectKey, boolean isSonarCloud) {
    this.projectKey = projectKey;
    this.isSonarCloud = isSonarCloud;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public boolean isSonarCloud() {
    return isSonarCloud;
  }
}
