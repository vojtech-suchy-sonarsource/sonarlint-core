/*
ACR-c3ee6302f37b40fda490374fe20d3fc5
ACR-670a80079a284c4381942b52c654dfda
ACR-1d75d1465038484081fbfdd959b4e67c
ACR-5c2b266a34eb4c098823451631fab924
ACR-c74a692dfe2b42888bfd07a3c4a43f23
ACR-58ac3c0a5a1d488086f469c0e03ea096
ACR-5d1eb650157e4a8483901dafbae15580
ACR-52d993c0de1346c88179250dadffb47c
ACR-7a81a8147db14ccf98ccfd78cd06ff37
ACR-a5a92b8561124a8bad729944996f4ad6
ACR-c13f4825793749cb8982865979be26c6
ACR-871328e3aa154814b348751340aa93c2
ACR-a3d660a802a2452798260a6edea2cb85
ACR-ab86e2952c7e4c8a9172e4ae1b0a46e5
ACR-9d25b1f32f4c46969ccbaa6c7900ccca
ACR-6ba9cbbe4f30468d9918c8e668c74b47
ACR-ae6f64b5c21f40baaae3579421c57818
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
