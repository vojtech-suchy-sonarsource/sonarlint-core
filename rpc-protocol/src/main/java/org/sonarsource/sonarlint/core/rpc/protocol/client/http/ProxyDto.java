/*
ACR-4d4243bef6c042329e98c1ca8951371e
ACR-53adc5c503b04a10a7c17026b7806039
ACR-811238cf72a64cad9db5736680d2d00e
ACR-24a503bcddda4df3b0ebb7929ee2f490
ACR-c18bd476aa6247cfb3b813ef8814b120
ACR-18e43c996b0a4d3b8ecb5d375fb790c0
ACR-198c822f5c6f40f2b479dbdd89dd17ea
ACR-a68cc0c60a53420b86bfa68f26504c85
ACR-009da8e60565453bab9bb29aee0eda18
ACR-c17fa47138c44157bc600b8e50d15562
ACR-a7a04f4a50a74d8790f02c3d0a526011
ACR-680798f9bbf84b3f8fec7267b985e026
ACR-0b16f102e2ba4a0498e9edc6aa93218a
ACR-d99c8a6bde7d4c7b97b15dd22d1cfd98
ACR-771c7b804f2b452398e4ccd92bbf7ea0
ACR-21f89f61375c46b59c4d3227b3ac8622
ACR-cb4dc2e05ff84fe9948bd910b0b9d0aa
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.net.Proxy;

/*ACR-69a392725083481faf344ffbe0e6ba05
ACR-1c23132c33e64f18a2312446cc8f1bd5
 */
public class ProxyDto {

  public static final ProxyDto NO_PROXY = new ProxyDto(Proxy.Type.DIRECT, null, 0);

  private final Proxy.Type type;

  private final String hostname;

  private final int port;

  public ProxyDto(Proxy.Type type, String hostname, int port) {
    this.type = type;
    this.hostname = hostname;
    this.port = port;
  }

  public Proxy.Type getType() {
    return type;
  }

  public String getHostname() {
    return hostname;
  }

  public int getPort() {
    return port;
  }
}
