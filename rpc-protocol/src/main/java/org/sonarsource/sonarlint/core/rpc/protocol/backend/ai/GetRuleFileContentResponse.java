/*
ACR-f5a0d06bcf4444f8994ff39be229296e
ACR-e7ac15c0dccb4d7f9e77d50dba53e3c9
ACR-c4aa594c1d334dccacf7a67f9e4f8435
ACR-d22b02ac57d440e58c9059cde7ce2869
ACR-a6c04082b67c400b90431f6c4f26377b
ACR-a14af3ea89324a29a685ba0db82f38ca
ACR-a562f74c919b4a03bb526823ad0cc408
ACR-83488ab75260400d966b4625a3a1e95e
ACR-154b3ff282e74397bc26917cbfe0e034
ACR-e8928523bda142a19b141954e545ac34
ACR-4955b03666294580b5f7f5bb0418255b
ACR-83cf367448d547e5b6da2dc798188fde
ACR-442d68bcac7941958aeebc4cc9c8389a
ACR-0a963aab8b1d4f88945511e644cd6c6d
ACR-df4e3eb318214aae9c272ae785f4874b
ACR-5c77c0c037bf490c92cc9975deb16cff
ACR-be293261303540e3a775d7ef37766fed
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

public class GetRuleFileContentResponse {
  private final String content;

  public GetRuleFileContentResponse(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
