/*
ACR-70bb1b71743a4242ba139779109ba10a
ACR-226e1b3125ef48f1bd9124bec197eecb
ACR-fd21b4156f70457597e7f29ba5a62f0c
ACR-0eeb7856d40345a08f3fcb6276cf5842
ACR-7aae1c5f8d394d3db8fe36031b09814c
ACR-279f0ed39f6844168e6886b0061c8644
ACR-296df7ee83e04842a3c8e6d2a2505c0a
ACR-f7641b4c71d74ecb84120a28a7d6210d
ACR-9c09cd34de1b463ea8f9026cca11e11a
ACR-53051ef7e22444fb9d9184c77d02c782
ACR-26593b3671534eb8b7045306dd780f5e
ACR-974a1ce429ea4497b93cb723c56e074c
ACR-8a35e6b9c9624236a255d2d6e6912e8a
ACR-33c24d58d29e4c0c9a22ecae3260a75a
ACR-2cbfc794ff114585b4e7fc8edf98461f
ACR-1706a4cf20aa48789b239376c4b0c06e
ACR-90114006e0854a2fa40bf21be19b14a1
 */
package org.sonarsource.sonarlint.core.promotion.campaign;

import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageActionItem;

public enum FeedbackNotificationActionItem {
  LOVE_IT("Love it!", true),
  SHARE_FEEDBACK("Share Feedback", true),
  MAYBE_LATER("Maybe Later", false);

  private final String message;
  private final boolean isPrimaryAction;

  FeedbackNotificationActionItem(String message, boolean isPrimaryAction) {
    this.message = message;
    this.isPrimaryAction = isPrimaryAction;
  }

  public MessageActionItem toMessageActionItem() {
    return new MessageActionItem(name(), message, isPrimaryAction);
  }
}
