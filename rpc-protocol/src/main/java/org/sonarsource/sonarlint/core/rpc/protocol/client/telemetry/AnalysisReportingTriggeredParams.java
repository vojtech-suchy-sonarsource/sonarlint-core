/*
ACR-0a7ffea1e5cc493a921aeee384de5aae
ACR-9b34c76eedae4f1cb1f221d069f89050
ACR-694822369a1a4d2588121c4d683eebe3
ACR-52b1aa6ba9004bc0ae57f959bbc863a8
ACR-0e1ca0f1258e43b891fbe26090240157
ACR-1c0c0417cc2e477190e122b8b1db5459
ACR-2504c19bca6e4c65abd01ef91dd759d3
ACR-59ecc34ec4a94f148d2b2c045d622d45
ACR-49ee7c2ad07a4cb2b1c042e1ae62e99a
ACR-d06699e9885d49059a878bf5aa42372e
ACR-6c9117b42f324956b74d28e106715e28
ACR-efb00e1830d24bb5bc2ce9a45a9c2fee
ACR-e420d24321a14ed486218acc6852c491
ACR-6840ac7d60b94aae878878e9597a2bba
ACR-faad3f4fce964f9bb44bb447110ac161
ACR-585f01c7401c4855bbd4a1d935a56f35
ACR-910742c11a2b4e2eb167f945602a4d7d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class AnalysisReportingTriggeredParams {
  private final AnalysisReportingType analysisType;

  public AnalysisReportingTriggeredParams(AnalysisReportingType analysisType) {
    this.analysisType = analysisType;
  }

  public AnalysisReportingType getAnalysisType() {
    return analysisType;
  }
}
