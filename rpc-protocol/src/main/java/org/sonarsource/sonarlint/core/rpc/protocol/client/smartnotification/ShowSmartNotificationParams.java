/*
ACR-be29a847795e48f4936fa06f2edc568d
ACR-9a34361362ce43b7838cd003ec6afbdf
ACR-5999f8fbebc848f880ecf935350fb83d
ACR-dcfff41bbf2f4c899eaeff56cc635447
ACR-b4fb2747d6fe4e66b7a4ee8f4723b9b6
ACR-6bdb61b3a6fd43ca9364c6746493f066
ACR-4865da82e6784cacb4a79e207b41869c
ACR-d2a2e20546c94319a43323f60be10126
ACR-aac93ce39f2a41b185a74572810ba4da
ACR-bd62043ca2dd45a6b4e438af71c3ab6e
ACR-b99fd0e6c3cf43918f1b525385b3ea99
ACR-aab4b5b6755a46d19420a3585c8e9bc9
ACR-b52c02d1dd004518b54ef8b01e30f4c9
ACR-fb8e519f21e64bacbd1c9865ee40c5c6
ACR-00f177137340459dbdacfeacdfc47aca
ACR-29c1f3db81524f7f84a674a3209baf2e
ACR-6daad7d0ad504d4cb271f0e69fa1bff0
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
