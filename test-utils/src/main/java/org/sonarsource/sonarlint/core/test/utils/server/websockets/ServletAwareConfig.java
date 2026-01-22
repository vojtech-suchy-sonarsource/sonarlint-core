/*
ACR-0d9b32e7a2544a1e9e464e031a317572
ACR-fd08c091477543c5a60d82db8d65349c
ACR-0842b50eef2a4ce7887ee2ebc9316eb9
ACR-8363ad427b1e4cf1947d5bde2bb0d6f2
ACR-9e7cd922a8f14ef0a0214ffbc0c85312
ACR-547be6f8d7b14b899867bbb8180c2b50
ACR-c3d383f5722e4803883862c1e6fb7471
ACR-ca4706a6d0d8413493e3cc0a2375020d
ACR-fdee73538fdb41408fe7a44858f44b0f
ACR-8cd579240e864a489ff207cd877330df
ACR-f09e97044ee54304a7adfd6bc3268080
ACR-c366251964fd406d8e179bf56e52eeae
ACR-3e4ab1f81f1144bda5cf87a1a93f05bc
ACR-0015a06051e744588d3f7c6ae485d668
ACR-45bd70bc13eb49e5a2d1609fbb110f17
ACR-161437645c7b4f8db76a63552b5d624a
ACR-36d8f7e36c9741d79351405202f9c50c
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
