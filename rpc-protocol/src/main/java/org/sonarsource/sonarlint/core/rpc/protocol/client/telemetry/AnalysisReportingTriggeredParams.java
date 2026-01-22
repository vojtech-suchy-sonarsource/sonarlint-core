/*
ACR-8dd21b76eb374d8baa06d01c4b7c5094
ACR-6f038d13e80445699ef12a53bceb4286
ACR-6da38be87fd1422d8e5c86ada5cb41cc
ACR-3a009abd7594430bb2ea8e72db160d66
ACR-f19954251eeb4c28abc701620fc8c972
ACR-db0e2fbd8b0942ba9d5150bf3acd7e31
ACR-5f36afcfc06f410b9f0e9da2563d3a3f
ACR-4b3f3e5361464d60bc32a09d815706b7
ACR-5e0b129f4f214b459c5cf91612c75a19
ACR-04576ccd276c4534a82ad0c36cbeb361
ACR-812337bede6f4d4f8dd16dd02c694907
ACR-379c11cb18b8480d92e76bc7fbd1491b
ACR-95be933c60064910849ddc7ad6832f89
ACR-1f10bf9a56d34d9a95ca0232e1de566e
ACR-4ff7ac8d654048c094b5ab7e7fa3f9e9
ACR-e1f1ef267cda4ec7b738dae2f2c356cb
ACR-e609b951eddc4293bdf26258c1cfe5bd
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
