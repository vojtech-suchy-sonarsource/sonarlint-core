/*
ACR-9dba5512f30340578badd48fecffeced
ACR-0d5015be85b54376a361310af7cdd436
ACR-5d932811f1bf4678bb4542f3f786d4a1
ACR-5d96fc7d850b430a960328be28424e5f
ACR-cc8578e775b84925b77d9425e653c82c
ACR-9dbd5a03228f4145a05a7b3765b7870e
ACR-1e6a539064e948b391119d831eeb396e
ACR-84c4034302b14957b0979106bd3e3643
ACR-572a7bbb9957485990c082c0ee0898b1
ACR-f8f8b3ae4898410f92065130a6139bfd
ACR-bced2cb022b04637942b41bb1a9e4280
ACR-1787300e8ffa429497ae2e992a382209
ACR-fc9050bba95443a5931ec72d915225fa
ACR-d828f5e4d72941efb1da6e11e2c9e7f1
ACR-935682856e6042caa38e1406a0537a77
ACR-7a5f627a4d0c4d1f83d0a28d7b3fa671
ACR-1e69d450d1a745b59bc5803266e60a33
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config;


public class DidChangeCredentialsParams {
  private final String connectionId;

  public DidChangeCredentialsParams(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
