/*
ACR-564085cd4ad54edbb7541ba58b84abe2
ACR-1568c0226eae47a08a7062031cf34831
ACR-034bfec605c44df7a363359e38e76b8a
ACR-9c0e5b6491b44d259e13785bb0b8f279
ACR-2d55cc48b16145048248a08ac1d3838f
ACR-440a577f903d44018dec7c424df103ef
ACR-cfb2ffb18ddc480ca982149a428c5ea2
ACR-b0d0363a242741819b5e8170eb7df129
ACR-81a7121c83544af3aaa80762fc8a3e2e
ACR-24d151c3dd4c4abfb2fe9c31e83a3618
ACR-a6dac851fde543149e2cefe83f498cb3
ACR-bd01b09cf9dc4999a9bd206dac093110
ACR-e911602a51a74bb095dd1d4b14a29af3
ACR-3ef66178b39f47e4a86cb21c2005f50b
ACR-b5ce8dbff572454da566d86810689546
ACR-0be22e6efe8843b89d86bb56428c9a67
ACR-8af009d296344eeda29f2db2e2e14df1
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
