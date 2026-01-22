/*
ACR-4f9a5e4934f640cd87fdb34ef627dd97
ACR-3feeccb2b43a413cb6546c8fa7fb1441
ACR-bdcabd47fb214a7a8bf4c1a748e214e1
ACR-d86dbbe6b0c34de4afba21b68976f7b8
ACR-52eaba862749485abb37b2ba98f8736d
ACR-05f55a562541442eaf7ec17e8e8e5d37
ACR-9cd823da50684b2c93befe370e924f5f
ACR-5127777f59ee440fa2b3ad6903c0b7ff
ACR-1f5715c880584f558700a396f727b19f
ACR-ba4277bd466346cea67945c4dc32f37b
ACR-816a695c4c8f4149bb270f479cb2adc8
ACR-52ee3cbf739043af87b988fe4e18a879
ACR-c2c427e0723d4e29bf4a35e33f6f7793
ACR-4f1a79a39b934d4b951561eecdaa32a5
ACR-25fac84624c5466ba2a97ca10dbef01d
ACR-c7721fbb79254ef4b0d2060264db0bf8
ACR-830cdf7f751e41328643c7fac47af7d0
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
