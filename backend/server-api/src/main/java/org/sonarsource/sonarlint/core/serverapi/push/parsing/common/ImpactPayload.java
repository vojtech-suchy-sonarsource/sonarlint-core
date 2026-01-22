/*
ACR-dc42c26310d84141bd2d2d387a0dac88
ACR-b4712ab033554c95abed4e0d7c3f7b4f
ACR-dc328a68bf804bdca62a89788b9788f8
ACR-1d4f5a7e160e4f32872608a784647fa3
ACR-609b9418b9de456f88ae337a88d7b829
ACR-b517b7474ae44994bcbcbae53a7b06f2
ACR-0586ca9813c2423196c4330c22cf71cc
ACR-a7e7fdf542114fc19924c706b1d08ea6
ACR-8c81002b18bb4092a21dfa1ab52a3a2c
ACR-ac0a1d4110124337aa1b6580a384e03d
ACR-f5ad6c72c53143ad89b97d0229c2cdd9
ACR-bc0c262f13df4cf5bf2f9b5f94422b50
ACR-3b5fe93fad3c4227a19ba1cae37746e9
ACR-39d701bff6ff453db29fcd4db34d028d
ACR-d8ded2d3e7de4ec49ca44e85f4c92679
ACR-5317660e9920456c8247a518d690f05d
ACR-d4d3ec621991411aa7c1d0b22c9d1073
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing.common;

public class ImpactPayload {

  private String softwareQuality;
  private String severity;

  public ImpactPayload(String softwareQuality, String severity) {
    this.softwareQuality = softwareQuality;
    this.severity = severity;
  }

  public String getSoftwareQuality() {
    return softwareQuality;
  }

  public void setSoftwareQuality(String softwareQuality) {
    this.softwareQuality = softwareQuality;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

}
