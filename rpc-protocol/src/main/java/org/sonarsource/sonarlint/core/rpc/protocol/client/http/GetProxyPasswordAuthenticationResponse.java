/*
ACR-16f940194f9240f8bbdc6d6b7387aef7
ACR-8662b9ac4d874096b0d78fb834122a05
ACR-58ecc81e39794838ac856853e853f1dd
ACR-46eee45754014e5bb8501d58da1bbad2
ACR-cac5700a303a4cc1a9227581d6d269fa
ACR-4e7aaab3e36a495089280294f0ea3294
ACR-64ba9c7accd64d17bacc7af19c5d594e
ACR-166a5487ce8646ff9d68298450b3c1eb
ACR-98b34710f562417599369135012b2a3e
ACR-5c27621dedbf4a6c953196c7f7783889
ACR-9465046c418d4cf0b99af4b3b3697ba4
ACR-53cc6cb4be4b4a18b7d0b3353596adb4
ACR-af9c92299fc044eba1d419a0d1f34bdb
ACR-e4bad9c8336b497d84e89941219f9775
ACR-9ad3bb8c8bcd4e23aa22d12ae607e262
ACR-07f7a8e3c1804d5a8c6ee54e4447b54e
ACR-53999e5b6793487ea26948ced2f6514f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetProxyPasswordAuthenticationResponse {

  private final String proxyUser;
  private final String proxyPassword;

  public GetProxyPasswordAuthenticationResponse(@Nullable String proxyUser, @Nullable String proxyPassword) {
    this.proxyUser = proxyUser;
    this.proxyPassword = proxyPassword;
  }

  @CheckForNull
  public String getProxyUser() {
    return proxyUser;
  }

  @CheckForNull
  public String getProxyPassword() {
    return proxyPassword;
  }
}
