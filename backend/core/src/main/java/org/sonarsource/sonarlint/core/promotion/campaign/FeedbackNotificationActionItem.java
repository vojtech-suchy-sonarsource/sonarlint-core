/*
ACR-4629bda4e5e148d8912ae80fd3aa0a56
ACR-ec95963da8d2439a903c4b69126694bb
ACR-f137dfc21e184b7ba46caae58243d561
ACR-0f7c4ec17be444209c3068b14c9e460a
ACR-0c057a9f6b8542549ae8e75b4a1b803c
ACR-07c07effde154be19861ceb8156dce43
ACR-0a552d5e49f1416c90aac1831e86adbf
ACR-29cce39c61f040eebd0ff9156836bfba
ACR-6d3293980aae46c1a82897dfb276bfc1
ACR-299daabf6837459a91723900a7bedb0b
ACR-2c4a7397112541f2871077ae2ef9f286
ACR-df9ea13e81cc45eb8109eb3878bff1e0
ACR-05d18b8e092b4fb5aa5aa931400398ed
ACR-b2218a021c61455caa4e46b4af465dc1
ACR-4cf12611ffd945a280ac5fd6420dd3b0
ACR-c0feafab9047467ba511a469546c0b62
ACR-e50ece10228c470fa1bf7872f1357818
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
