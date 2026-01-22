/*
ACR-e7188b3e110042fe90777b6027dd0fc8
ACR-40716e1030b24a829aeccf3a80f9e3d7
ACR-0f305d7963fa42fb88782bc8feb5c7ae
ACR-8794fd33aa394f909fd322dc396035c5
ACR-569c05932eac4ecd8aa92f87b43f443b
ACR-305ea83eb6b444e58da234dd1bd04ec4
ACR-a158ce506f064925b66dbbd818a963fa
ACR-254ef7be0f294085ad58bcc919c5a8d7
ACR-ee920822dfc94cd9b0c61a6e9c653160
ACR-996c41c87b1c444daea0f59202437d37
ACR-f544da75024d4bbcb4306be6cdb059ff
ACR-7cbb42a13ecc406ba756c2aaa316f5e7
ACR-623b74a4901b4026a88a5f90b7019377
ACR-df6d90e197584145abc9edb99e9c5925
ACR-c359b25d6d3747df95310e82554aff38
ACR-a3efbbe3f7474d22a777414a37cea2c9
ACR-4d43bf47c64d4df395f0e74bf0e615b7
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

import static org.sonarsource.sonarlint.core.test.utils.server.websockets.WebSocketEndpoint.WS_REQUEST_KEY;
import static org.sonarsource.sonarlint.core.test.utils.server.websockets.WebSocketServer.CONNECTION_REPOSITORY_ATTRIBUTE_KEY;

public class ServletAwareConfig extends ServerEndpointConfig.Configurator {
  @Override
  public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
    var webSocketRequest = new WebSocketRequest(request.getHeaders().get("Authorization").get(0), request.getHeaders().get("User-Agent").get(0));
    config.getUserProperties().put(WS_REQUEST_KEY, webSocketRequest);
    config.getUserProperties().put(CONNECTION_REPOSITORY_ATTRIBUTE_KEY, getWebSocketConnectionRepository(request));
    super.modifyHandshake(config, request, response);
  }

  private static WebSocketConnectionRepository getWebSocketConnectionRepository(HandshakeRequest request) {
    HttpSession httpSession = (HttpSession) request.getHttpSession();
    return (WebSocketConnectionRepository) httpSession.getServletContext().getAttribute(CONNECTION_REPOSITORY_ATTRIBUTE_KEY);
  }

}
