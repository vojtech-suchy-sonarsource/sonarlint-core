/*
ACR-e209f5695ca244b9944e6da2f493a68b
ACR-3eaf21904bc0448994215c1af1794fa4
ACR-8bb903d018604af7a153d1417a3d2fcb
ACR-9fdba66ae3954127bb2300d5edf42768
ACR-77807dadba584151bc35318a76b42b08
ACR-6c07abe93c2f46198e06222e798043c5
ACR-a3fc02dd47424f2fa401a1712b096559
ACR-a27f27d959a74c9dacfa51dddbc94e9f
ACR-c80b378072e3413099b5ff4a83dc71da
ACR-39f6cc5d6234402b96d7a03ceb636ecd
ACR-de30687d82bd4a39b18d39a29e048055
ACR-6c85b9f26c4146b58b5bedf703130ea8
ACR-634ba5ace08c4a84835a7d6fb25c4f1c
ACR-cd62cae5075e486eade6796e394d994f
ACR-da9b5d25abbf494d9e68dd739070f701
ACR-44c0ccf47e964db9837cec4b807ad284
ACR-c513fcc9ba504f998091fd3c67c43c9b
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketConnectionRepository {
  private final List<WebSocketConnection> connections = new CopyOnWriteArrayList<>();

  public void add(WebSocketConnection webSocketConnection) {
    connections.add(webSocketConnection);
  }

  public List<WebSocketConnection> getConnections() {
    return connections;
  }
}
