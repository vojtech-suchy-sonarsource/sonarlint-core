/*
ACR-fb653e9229624f67bf77255911e44219
ACR-992128f5d9bb4bf48482837d941736bf
ACR-00a96ded0c874ba087b6032d37c3d66c
ACR-a4d9315a5b3c45fe809e0974926ec47e
ACR-b4929127b4064a6096ca63155dc0768a
ACR-e7360206cabf4ef2803e4b06b25c6677
ACR-bbdac39e5dc3459782969a38bf2f2274
ACR-c7e71ef9dfcd436f963031c02bf91619
ACR-3feea9c999ba40068e6d25c4a5613f49
ACR-d21a2641ce4041d6a0d85af7b97484d1
ACR-a2a91f8ac0e74e8187d980d4de77f4f6
ACR-90298e8b304945ada4b7196640416921
ACR-68382f72102e4da88877632fe3b64dfa
ACR-b053500dd3a740c49b6b7e419f7dfc2c
ACR-5196c43527d044479a2364de784fb7cf
ACR-14274fe602f843448ccfb630b5a3fc6b
ACR-840c637999b246dd978b5e3087822ec6
 */
package org.sonarsource.sonarlint.core.ai.ide;

public enum HookScriptType {
  NODEJS("sonarqube_analysis_hook.js"),
  PYTHON("sonarqube_analysis_hook.py"),
  BASH("sonarqube_analysis_hook.sh");

  private final String fileName;

  HookScriptType(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}

