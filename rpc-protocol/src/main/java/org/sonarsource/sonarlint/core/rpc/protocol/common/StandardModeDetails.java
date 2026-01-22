/*
ACR-30e9f8f740154d59b19069b38179fb0d
ACR-e167ecac39a641f28b6b942129127b06
ACR-e1b225f61ad742e4ac2d5c36106525c0
ACR-4d460be024cb4b4dafb8d1aa1958a307
ACR-aba0325850bd49babcf3eaa50351a50d
ACR-7caa9e921e0e48ff998a0f67e68548a6
ACR-a76887afcfdc435cb147904b59ba784c
ACR-0ad7756e6edd4efeacac90d77cf843ba
ACR-ca24dd3c447547fa9090849ef89620ed
ACR-ca906d75289c44edb91d8cfc0017735b
ACR-84113f286b0a4c81b85e194fe3612953
ACR-73cf765e63b54738ae766ade459620bc
ACR-c4e09643c2414d5183037a351d9349e3
ACR-35229ecfc4aa46b6a48e390e5768529d
ACR-c6f64633335349a3af6058be7640ecd5
ACR-c0f0125d961e4b85ae48381283b2f2ae
ACR-1c8531455b354e9284c937bd574520e8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

public class StandardModeDetails {

  private final IssueSeverity severity;
  private final RuleType type;

  public StandardModeDetails(IssueSeverity severity, RuleType type) {
    this.severity = severity;
    this.type = type;
  }

  public IssueSeverity getSeverity() {
    return severity;
  }

  public RuleType getType() {
    return type;
  }

}
