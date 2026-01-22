/*
ACR-0162f873c3a145b3b4deedfb5b171271
ACR-a2bebf03d9ac48acbcbf389063ffc010
ACR-005d4079d4a54e0899aa453f2e6c3fc1
ACR-0d45e91a4bfc4b14a075d9ca5cf2b5b5
ACR-781968eca06c4bf28817048b4ed275d8
ACR-38e57423544b44aeba2de2e65436fdfc
ACR-485562c1262f403a8d996ad3482d63eb
ACR-cd65566a4a8245d38db1ad7b98eebefd
ACR-4c8bd78656684b2d892198ee0133b044
ACR-91e183a0de254abbbd3ad72b5e0b8a89
ACR-e89731f116894153b31348a502adf1ad
ACR-5e5ea37d80ee4807a19f653e773a993d
ACR-0ea68c0568bf4cf8be8500857b8edeae
ACR-2ce003e995e543b6afcf86864c4e08e1
ACR-831cb54ec26e407fb0e5bf5ea6b92e76
ACR-07c1f2accf72400e9b5ce46964832e3b
ACR-b0a33ff239834d89a645ebd1d2a1fb1b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.net.Proxy;

/*ACR-241e26d111d045a89b6e0c2549b28ba6
ACR-a6d1f1cb27224042ab1d754d2963f297
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
