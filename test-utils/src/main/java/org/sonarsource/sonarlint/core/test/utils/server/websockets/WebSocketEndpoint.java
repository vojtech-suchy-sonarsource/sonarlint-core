/*
ACR-baabea3382f84596889a53800997c2bb
ACR-483db5d2f6ba4beea2bee568ea425f95
ACR-1212288b7e914e5fb16b89bb8cd8bc78
ACR-b1058e2249f64d909fd860276623111c
ACR-87d5878ba1f64c69a3c87107bb7bade3
ACR-2c2f11d99cc843fc96e65217737c1451
ACR-e32775af441944e79c55021ff1a267de
ACR-0a088d336b3a4ab2b9dfd0aaf662038e
ACR-327f9c24ed0b495f92e02774d22a793a
ACR-a3a19279c8ca45b5b6b7343fea25d0d5
ACR-0a8d670847ae4799b533932aa7b44256
ACR-c700e56fc69a414db1974830117ac5e3
ACR-86283562ccb04ac98f26d81f9b623308
ACR-36c89fda256b49639c66eba7b0945512
ACR-f98180f24020437aaeac69dcd35abbd6
ACR-562ba8af45b048ef916e39162f5a474c
ACR-0f3140150fdf4e32ac3dca5747feee1b
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import static org.sonarsource.sonarlint.core.test.utils.server.websockets.WebSocketServer.CONNECTION_REPOSITORY_ATTRIBUTE_KEY;

@ServerEndpoint(value = "/endpoint", configurator = ServletAwareConfig.class)
public class WebSocketEndpoint {
  public static final String WS_REQUEST_KEY = "wsRequest";
  private WebSocketConnection connection;

  @OnOpen
  public void onOpen(final Session session) {
    connection = createWsConnection(session);
  }

  @OnMessage
  public void handleTextMessage(Session session, String message) {
    System.out.println("Message received by web socket server: " + message);
    connection.addReceivedMessage(message);
  }

  @OnClose
  public void onClose(final Session session) {
    connection.setIsClosed();
  }

  @OnError
  public void onError(final Session session, final Throwable throwable) {
    connection.setIsError(throwable);
  }

  private static WebSocketConnection createWsConnection(Session session) {
    var connectionRepository = getWebSocketConnectionRepository(session);
    var webSocketRequest = (WebSocketRequest) session.getUserProperties().get(WS_REQUEST_KEY);
    var webSocketConnection = new WebSocketConnection(webSocketRequest, session);
    connectionRepository.add(webSocketConnection);
    return webSocketConnection;
  }

  private static WebSocketConnectionRepository getWebSocketConnectionRepository(Session session) {
    return (WebSocketConnectionRepository) session.getUserProperties().get(CONNECTION_REPOSITORY_ATTRIBUTE_KEY);
  }
}
