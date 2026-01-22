/*
ACR-49138b6533e24e4aba242842ffffe780
ACR-31e47f31aaf646a2be162b99c49f9d20
ACR-7d7076faceaf45b4962492b45c5d08e1
ACR-5bfbab5ad87c48ba9dcaf67d976c53f9
ACR-f96a05ed82da4d148515e4ebbc051f08
ACR-7fa6324a8caa402f96e9d62acdb651e4
ACR-a57c4268952849f188be8197d733a3e9
ACR-25899736dd014455847271dcac9b38cc
ACR-19cbf9d8e6344ec4b8fcc99e31aba064
ACR-38a5618ae4654f7d9ed0317540bfd3ed
ACR-31dcf56e7dcf4fdd95722b06bf3d640e
ACR-e4368ea7df6d4951b6519f5a7648c280
ACR-5fb9381edc5e47da9602664b4e1f02df
ACR-b15e339a4b7542a79c63646a346ddfdb
ACR-3fb9f66c6e5242df836da5214c90572e
ACR-797dad5045c9482aa4ba92c434dd3573
ACR-b449aa13f4534aaa805c77210fa17c40
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.util.List;

public class ClientInputFileEdit {
  private final ClientInputFile target;
  private final List<TextEdit> textEdits;

  public ClientInputFileEdit(ClientInputFile target, List<TextEdit> textEdits) {
    this.target = target;
    this.textEdits = textEdits;
  }

  public ClientInputFile target() {
    return target;
  }

  public List<TextEdit> textEdits() {
    return textEdits;
  }
}
