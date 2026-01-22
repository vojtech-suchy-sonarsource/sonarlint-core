/*
ACR-a1f5a3c9fb8e47bba9ccb106e685bc4d
ACR-bcd3d8b1c0cf4272a9d618785e81130c
ACR-381d445bc3244d4d997d05adc557c851
ACR-b4c61b34f13847d78b3851016c967dfe
ACR-78c8837e8e394a3ab8aaf603444911ab
ACR-a653a37af4a34dec8418b72aac384cff
ACR-880d873ade0b4a8597751df6646940cf
ACR-85dd91400d694dbebbeaeda87846bb63
ACR-45504c8188594cbb822eab0925d7f7d9
ACR-e2ed1e1329524659b4e5c4e1ed9ee35f
ACR-b59b21c0074c4b8fa8aeb97d1ae27ff2
ACR-d449d1843300468180d3f173869266a7
ACR-7d7a609e42324c5ca14fad3ae0366186
ACR-3bd09b05355441e6959fb19553e9a260
ACR-3b2771deb4e44ec29ad0002a4993cce6
ACR-3d7a7a2827da4539963b1127b49d2344
ACR-2dedd1cacfb44326b98f9b89656591ba
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.labs;

public class JoinIdeLabsProgramParams {
  private final String email;
  private final String ide;

  public JoinIdeLabsProgramParams(String email, String ide) {
    this.email = email;
    this.ide = ide;
  }

  public String getEmail() {
    return email;
  }

  public String getIde() {
    return ide;
  }
}
