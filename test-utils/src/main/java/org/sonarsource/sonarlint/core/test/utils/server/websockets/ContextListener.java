/*
ACR-7f188f14e2b44b6abcd2932a1ca70eb5
ACR-bf304365e4ba4447981abb19f444fcd4
ACR-90ff1bf71e1d424389a5be84ab93e2b6
ACR-3b341657bff04ca6843cb2f400cdb836
ACR-bbb177f694f04d0eabc1d4866aab4f66
ACR-515b693fcbd9468aa223ee8f036d61a2
ACR-b0907a38ef6f4a5f98d0eb7ea62daf47
ACR-f72234fb87a04a53932e2f1b8f2cc7a1
ACR-1c714c31e99b41a193f91e1ad785c69b
ACR-7036f9b5202947dfaeefb5077cf89cfa
ACR-c28f1b3f64514bfa86094cd7911bbfc2
ACR-d8dfbbd40ca54c06a0f87a7816226285
ACR-0b271ad3bf2e42be8a1d0417bbf68d6e
ACR-232ea1d5242d4d61a40bea522445edd1
ACR-7d2a21dae57c4f10895e82e03bde4642
ACR-4aac2c11875147f59d10c0b0aa1e8175
ACR-f932832e29d44bf5af5120dba99fa837
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

import jakarta.servlet.ServletContextEvent;
import org.apache.tomcat.websocket.server.WsContextListener;
import org.apache.tomcat.websocket.server.WsServerContainer;

import static org.apache.tomcat.websocket.server.Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE;

public class ContextListener extends WsContextListener {
  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    super.contextInitialized(sce);

    sce.getServletContext().addListener(new RequestListener());
    var sc = (WsServerContainer) sce.getServletContext().getAttribute(SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
    try {
      sc.addEndpoint(WebSocketEndpoint.class);
    } catch (jakarta.websocket.DeploymentException e) {
      throw new RuntimeException(e);
    }
  }
}
