/*
ACR-e34b2af420974696bfef33b527515226
ACR-100779de018a4cc38e58c43ce9a19806
ACR-b708a45b00fd40459c02b2ebbcd3748d
ACR-3b032c98e3364f4f95fcd5e433f5c04b
ACR-b9896cc8ac2a4f4aa7edcf98518cea35
ACR-4c0d295123bc490d9e6e3d286737f670
ACR-86ce9ebc3cdc4597947025101bfd0cd6
ACR-be76a3079953406dbb040a818b41625b
ACR-746ba270b74d461ca2d4943ccb4ea06a
ACR-aeb1796db99f4d258a07a09c505a96c0
ACR-9b0b0e56a9b64a88ad3ec2be31bece8f
ACR-e70872b4215843fe8781c534a116df58
ACR-70f8e93a9f0545fda665453749ce872b
ACR-14c50f25db834e07a2ae23092b059a28
ACR-6c02453e73254b488e8ca562e021063c
ACR-4c71686fa2194a6e8d5e867df2afdf55
ACR-c71a2047b947496ab5116009515252be
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
