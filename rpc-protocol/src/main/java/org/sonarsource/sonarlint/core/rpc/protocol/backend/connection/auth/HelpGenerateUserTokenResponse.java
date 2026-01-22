/*
ACR-51c39cc991504d2f88f61c0009aeed4e
ACR-daa36bca087c4e29b61e869101439d0e
ACR-9ebfd57d3a81413882859f6f25082199
ACR-b59bbc190c8a4f7cbd5c6fe2a18066e1
ACR-5522d5fdea3644cd969f554b0f5b480f
ACR-9007c1b4d0514562a0dbb30eb7f9ae27
ACR-d7127891a53d45c4a11d9ba6982b625f
ACR-b316bafcf03541c88055a8a263276ae2
ACR-e5cff8097aa34f3788d22f0465b4f0ce
ACR-ac23af66b81b44718b594ad2998692eb
ACR-5ff4378d98784931aab6efde77741bdf
ACR-c7cc3b31b52342be8d3ebb67e411850e
ACR-2ace3a8108b04254b351b725a1a75768
ACR-c4906014bc964ef58e40914c0c40d2de
ACR-0cb618134cb2499196dd568bcc60229f
ACR-760259256b3840af99c09c159dfe1362
ACR-3b121da98b644bcea2d09641ae08e080
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/*ACR-8879dc93fe2d472fa208bd72e9aba652
ACR-192ff79f3cd743ea89fa1d5ee4ba6fca
 */
public class HelpGenerateUserTokenResponse {
  private final String token;

  public HelpGenerateUserTokenResponse(@Nullable String token) {
    this.token = token;
  }

  @CheckForNull
  public String getToken() {
    return token;
  }
}
