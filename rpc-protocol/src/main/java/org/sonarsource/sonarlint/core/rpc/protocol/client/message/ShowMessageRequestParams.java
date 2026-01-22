/*
ACR-e3bc3f126a25472c957083b5e06aba0d
ACR-dffd92777e0d43a1b1a3133ee62c0d60
ACR-2baaee0df48049d5a21269c20658adb5
ACR-f81f7eff95e946fd8e9fb2f22ef26d8e
ACR-ed42eac8a9944a99aa5c12fec380f331
ACR-e4c851d327854ac78fa939bec8244f1d
ACR-122846f8f71a477f9fa64ecc91ca967e
ACR-f4d6d0d79f3640538e161f25aed696b9
ACR-2bc3ac185d024a6594c7cbcc08f3ca17
ACR-9c846d091d1e49da98f8e76b8f53531f
ACR-526f91f9b8984b6e85c9efec6fff5fca
ACR-776bf5058cf648a58253b5416e52e242
ACR-2f65c578dd414f8aa1ad7d989388f208
ACR-9281c77c3bad4d4282f579e3e2f35b9a
ACR-76a8f9a7bf2d4bd5b11f5908aadeab3e
ACR-3bdafa286315490d8fe5583b5af3e7af
ACR-41cb05a96e8b49ec97a4ce5ee3c72e1c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

import java.util.List;

public class ShowMessageRequestParams {

  private final MessageType type;
  private final String message;
  private final List<MessageActionItem> actions;

  public ShowMessageRequestParams(MessageType type, String message, List<MessageActionItem> actions) {
    this.type = type;
    this.message = message;
    this.actions = actions;
  }

  public MessageType getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public List<MessageActionItem> getActions() {
    return actions;
  }
}
