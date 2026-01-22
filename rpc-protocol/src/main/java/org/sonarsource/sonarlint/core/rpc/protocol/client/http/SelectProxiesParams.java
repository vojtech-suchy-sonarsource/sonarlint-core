/*
ACR-e6602d01dee74bf3a23488ed80ac7ec8
ACR-5f900158424f4d5abe0832e814a2ea44
ACR-da94cd57001b4e78b2febace43a13a96
ACR-1e86938ec63f4096968484543fc9b026
ACR-5cf46f7ea6434bdfb298424e5589882a
ACR-a08cc06b29d64389b76ae79da320c72b
ACR-9ae8a10f8f654ceabf0196cd795f6fca
ACR-2eb60e01219d4fb2a3fdd265d211353a
ACR-bd97feb42cea465388cc1bf420ca5eb5
ACR-7d814628f83c4b2cb17e4ffc97d7a6c6
ACR-1894649191d340ffbd171fe43a14c6ae
ACR-5c7e1c731b4749f094ece1cf82ed18db
ACR-1b65735236eb4c51914e36f445d7e855
ACR-3c91d29d05d4483792ac8457927ea212
ACR-33c5516a0f7c4e53b57cb1e08ef8ffc6
ACR-cd6233e9b86d4a1dbbadae2a7e81468c
ACR-4199bcf113bd4f87a6ac599957b71582
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.http;

import java.net.URI;

public class SelectProxiesParams {

  private final URI uri;

  public SelectProxiesParams(URI uri) {
    this.uri = uri;
  }

  public URI getUri() {
    return uri;
  }
}
