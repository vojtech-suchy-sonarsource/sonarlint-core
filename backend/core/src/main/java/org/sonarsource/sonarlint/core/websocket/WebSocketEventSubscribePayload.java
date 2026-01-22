/*
ACR-ff500ea4ad7b4598806173409b3fbf51
ACR-e82ae3de8fc04e8793bd87a52ecea232
ACR-11c0338ac0bb43b99ab75770c4d8f9d9
ACR-b8c5dc8ba132468ca66d4eb255f85bf3
ACR-ec6f37050d434ec18b2b1e0551218831
ACR-8fe903a2023049609113a506949f2c9d
ACR-272bc3d6226d4fe685e97d4c3643c034
ACR-0f5d9786a4c84a39ac14a10da0c1a9bf
ACR-84619df7351d4c3e9c061837576ebd6e
ACR-078cf35982fd49048727b25f7a61d57f
ACR-836cd550ed0e44ae959e3de1f7c9870e
ACR-2bf0d9ec3b6443959293cf8261000ae0
ACR-1cf4befb5be9452f9de754a222e871a7
ACR-044b491e93e740c0b2368f359df485c3
ACR-7a6d6a9941a14eb4a985d8355101c627
ACR-6953506e767940d9a1c1fae45f23209e
ACR-cadb1f4fab5c4a12a719f590da07cd4d
 */
package org.sonarsource.sonarlint.core.websocket;

public class WebSocketEventSubscribePayload {
  private final String action;
  private final String[] events;
  private final String filterType;
  private final String project;

  public WebSocketEventSubscribePayload(String action, String[] events, String filterType, String project) {
    this.action = action;
    this.events = events;
    this.filterType = filterType;
    this.project = project;
  }
}
