/*
ACR-38ff86cffd7644f29bce145888f2474e
ACR-bfc2bd2a94184d94bf015fd7a33f25a9
ACR-96502eebbebc4e2a83f07e6d3bd92b7a
ACR-1b6345484d63438eb5c4906dee0bd75d
ACR-1ae5f69b2eac431fadb61b423ebe2221
ACR-b2911daa9d184068913797ee5a30623d
ACR-d6db04ae33c74a7a956a8ed5131f5aa5
ACR-b095609a9dcc4392a678425c4146b2b9
ACR-8ce0e53712694b7db53ed8ee52f0082c
ACR-8a27c01fed0c485fa322dd2443a695bf
ACR-db7ae5c195e24d53b8667255d45ce27c
ACR-cdfcf5b42d7d426d8a004b2b00e536f7
ACR-e58816d626564d2eba4247563697e180
ACR-d1f2792ed36a4a388d5c1eaa7ce881df
ACR-5be0ce83a9684c66ab8f61dc64a43cc4
ACR-4e5d0f793e014b0fbef72b4c35960505
ACR-18aca5b1d21b419dae89904333c87978
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.util.List;

public class CheckServerTrustedParams {

  /*ACR-7f3083d6a64447a4942cac44734c5d0a
ACR-c54e8f0e3c01447d96db9729c62b0b6f
   */
  private final List<X509CertificateDto> chain;

  /*ACR-9f9fec99a0794aa1b542bf5d7bb94525
ACR-4b4d6e0fb9f94314bef888bb20a99779
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
