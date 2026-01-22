/*
ACR-b070808c99e34f779255a9e4f2be456b
ACR-ad48e04152a2497fa906d6f3d5cac056
ACR-d3606f988ad6460f94404e9e597b1600
ACR-b46e9d5e08484c74a84bb0d1e10e4ca5
ACR-6e7d72ce23d14d0b91b25aa508966352
ACR-a4b92c9c933e419ea96a85cb30e092e9
ACR-83aef41818684c9fbb69713516ae7d1f
ACR-467bdb38d596444db24d65b1688946eb
ACR-036295e64bdc48b1b51b96c435c7fbe5
ACR-47b32f25653b4676a28222ffe0825b9b
ACR-b81e4b5e7f9e454ab605a580fb9e74a8
ACR-9d43d650e673486994dc37170a1df019
ACR-63cb0842cc1c43daae56aec204dd663c
ACR-416e2eb293144ed7b264f9b5b0b6ca47
ACR-12f9bf784b284233bde7ebfb76f19be8
ACR-69018d0d0b38446891f6ac3e161eb8f8
ACR-3b822341913142048d9a34fefdfa8920
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

public class CheckServerTrustedResponse {

  private final boolean trusted;

  public CheckServerTrustedResponse(boolean trusted) {
    this.trusted = trusted;
  }

  public boolean isTrusted() {
    return trusted;
  }
}
