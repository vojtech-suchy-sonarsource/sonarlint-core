/*
ACR-7366ab761af943ce8ea1b5f0bc3b19b5
ACR-1fcc20502be7495f83994c9892e30557
ACR-8d6cca7bd3014ba89315e1065a3e801d
ACR-f7e10ec3d93a44b4bb4d1147a30823ce
ACR-05e1242a96b9407cba6482fde02211ab
ACR-0f28c84e28384e4da0457278cadc18de
ACR-6193583a9add4cbd9b79f3f6b625d204
ACR-08421c097ee842b191939e34766c8d11
ACR-8cf0ddf1cf2c41279d89c51d71934173
ACR-cb32093c9068491faf318d53ac9aa17d
ACR-be19dcbd1dfe4c618ef4797ffbf8659a
ACR-9794970b3ef9452398c38e8a97b0dd36
ACR-2862ec3003dd49a4840e9abf0e29fb51
ACR-fb7bbb58f3d14b2091c1f6dd12d87b00
ACR-f1c9824a182b4b4580f119bc3bc17c40
ACR-d776b0948719495e82614c46935e86df
ACR-353b96ba60854ff683005fcdac933fae
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

/*ACR-9f1b9e4f261f47379577015a1d495f42
ACR-83fe4f29f3784176bb1159e91cf1556c
 */
public class MessageActionItem {

  /*ACR-817454b555934cdc90287e91b0c8d69e
ACR-a5b02c9db5814ddf9143b3a25df6cc9a
   */
  private final String key;
  /*ACR-2cea587238d94bff9304236345dab64c
ACR-627abe32097c4b52b11fe42855187491
   */
  private final String displayText;
  /*ACR-d0c8c0d5119a4eebabe1636e5b076e82
ACR-a227389f9bda401782be6e6a1dc47b2b
ACR-b22424284ae94384ad1b35f7dafeec80
   */
  private final boolean isPrimaryAction;

  public MessageActionItem(String key, String displayText, boolean isPrimaryAction) {
    this.key = key;
    this.displayText = displayText;
    this.isPrimaryAction = isPrimaryAction;
  }

  public String getKey() {
    return key;
  }

  public String getDisplayText() {
    return displayText;
  }

  public boolean isPrimaryAction() {
    return isPrimaryAction;
  }
}
