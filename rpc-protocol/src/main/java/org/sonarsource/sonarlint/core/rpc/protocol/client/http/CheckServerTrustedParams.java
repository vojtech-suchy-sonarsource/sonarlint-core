/*
ACR-ca879c9359ff45dd9b3c7f08f55f0673
ACR-c614fd0fa40b413ebe0255c3c220a65e
ACR-ef9403e55cd74a8f9a830e7b96b10cec
ACR-26db0ca4980645179b96ee2a0b1420ab
ACR-b044681b02d048718234e0e4ede89221
ACR-b3b9bc20ad9b4c35b03e9b174d902d27
ACR-262849375fbe47c8b54f3d9e5be18bc5
ACR-94f56e84c49a4eb6a0d650fd1db19bbc
ACR-83ccb9b4488c41168bb9df8a12105514
ACR-a2932fe02243487bbfd66572e0ec0f4b
ACR-2e7fdb6f60ac4935a70c73a869df7025
ACR-251a212c03fb4b939a766696b625af41
ACR-d559c54349ca402985fbc49e17db8fdf
ACR-874f3d33e703456cafe479735072e9a0
ACR-625227fdf74e4a4885ae7930f5cb2a9b
ACR-2c0a17d9456748d89c8b7d50eae9592c
ACR-277413c859ae48c0bcd0141b4a317490
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.util.List;

public class CheckServerTrustedParams {

  /*ACR-5188b0ac9dd54ca58ab9c7318ed9e744
ACR-65dd293dc4914dac8382707122344e29
   */
  private final List<X509CertificateDto> chain;

  /*ACR-869a6292c72b47aea7d632d9cee9f125
ACR-7d36025094e74be789af5f3cf6ddd8a9
   */
  private final String authType;

  public CheckServerTrustedParams(List<X509CertificateDto> chain, String authType) {
    this.chain = chain;
    this.authType = authType;
  }

  public List<X509CertificateDto> getChain() {
    return chain;
  }

  public String getAuthType() {
    return authType;
  }
}
