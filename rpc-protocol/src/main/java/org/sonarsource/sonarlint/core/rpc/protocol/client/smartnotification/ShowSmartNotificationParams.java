/*
ACR-ecff10c8febd444088ccaf884b166c08
ACR-f711314cad5d463d83431d198fa14459
ACR-3498da7d93ca4175b2c3b6d0c79fa0ab
ACR-2b5bc4c75274439db5fe41e52b651a5b
ACR-2d1ecc51ed3a4dae994b4de94941b203
ACR-aee2e7f2421c44d88f4cee23ef805a29
ACR-332db6f775ec4e41ae3e1ed0d22860b4
ACR-7ea3d8203490497f99d9192e6f263a29
ACR-13fc58bdf8d246bba50d96a41ad9aaa3
ACR-aa33a201861b4d1586a6b94d64b42b31
ACR-d81c4f768e52472287ca5c40fb5a03f5
ACR-61d7ca543ad34165bbd391f3d53d475a
ACR-3df9599b1c45459ba8aefa6f32a4ccee
ACR-1cfd131758f3419892572372ab8741e8
ACR-b77c9ffac86f4ffd9f96344e1bccf4d4
ACR-03917154f3cc494c92669d5b88d7f759
ACR-ee0f81211313491eabfca81b98c87bfb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.smartnotification;

import java.util.Set;

public class ShowSmartNotificationParams {

  private final String category;
  private final String connectionId;
  private final String link;
  private final Set<String> scopeIds;
  private final String text;

  public ShowSmartNotificationParams(String text, String link,
    Set<String> scopeIds, String category, String connectionId) {
    this.text = text;
    this.link = link;
    this.scopeIds = scopeIds;
    this.category = category;
    this.connectionId = connectionId;
  }

  public String getCategory() {
    return category;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getLink() {
    return link;
  }

  public Set<String> getScopeIds() {
    return scopeIds;
  }

  public String getText() {
    return text;
  }

}
