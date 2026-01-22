/*
ACR-b463918f49cd42b0bac127b0fd850c35
ACR-fa9bd2d1880d41a0b26f8ae842c8354e
ACR-242d35d13f7f4980a5ba7c77049a0e5d
ACR-f4360dcf455b4bfa984a66898931388b
ACR-0741110cb1a34f00b3d78115538fb81c
ACR-2219da5f531d4d1e9c39c5c7b3b6eaf5
ACR-10b6db37ab8142d2a546b4126d12d8f9
ACR-aab66171efe34a5d9b151dd69f01aed1
ACR-5a41f3cb720d4815b1c346ca1b244a18
ACR-74196975672c458f84d9dd878035bc11
ACR-2f87670ec38f457aa502154ac0578bf4
ACR-9ff47aa3e0ce4bc38f9a0dcb47fe1b34
ACR-9be79948dc53414ab79297c003d0851c
ACR-350dcc63b6434e56a9315f4407cb24e3
ACR-ba9d167f5f934a5483af15ba77ffe4ed
ACR-f54c2dd070d24fea81dd1affbaadf145
ACR-90faa9cfbe1b462c8455771e5fb56aa8
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
