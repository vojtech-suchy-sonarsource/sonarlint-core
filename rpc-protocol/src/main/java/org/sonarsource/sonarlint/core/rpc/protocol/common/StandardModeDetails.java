/*
ACR-9f36ee1e8ead47a9b9f2d24ef939c926
ACR-d1a6b027ea534176a7cb441a90071191
ACR-3c3548e4a2e6493ebfefe6610f8fd013
ACR-3132969b981c43699c732e013c25b2f5
ACR-51cc07b274a64b069340cda615bd4067
ACR-0f25a052ae8b47a6af8da8446cb60659
ACR-04161e40ca564afdb85eff60e5ee6a57
ACR-59a956613d25480b9f83cacf0aa5e7d1
ACR-3507d997d551444b94c697e9f2d9f2d6
ACR-c26b2fe7506c4786a6ad34bfe91fa270
ACR-3c34641490cc400b8b7b72061253f9c2
ACR-43c69c5d93dd427c8fc0c476d714f490
ACR-1b224fb9172546e0a81f04d5c9a6f58b
ACR-bc0100ab49164cd89465de64631572bd
ACR-c33d20240a32454d9f557969d8c8430a
ACR-e31ee77be3304b5eb556ddfe85bec1d3
ACR-4c5614e196c148e0ba0b7f4a9cb7b0b1
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
