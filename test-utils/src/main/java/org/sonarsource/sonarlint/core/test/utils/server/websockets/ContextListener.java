/*
ACR-3b1acd13dd7c41378a682e8eb6c2f713
ACR-5b13202fa6a24f94b9dc812c8f2f84f7
ACR-5d7e2c2054bd46aa92ca4b0180b7a52b
ACR-3a03a790c4d94c43a538d6e653b17a4e
ACR-3e286cb7541c404f8d0785c56b75cee7
ACR-e7ffe67ba155466ca5e5db6f1d884d19
ACR-8722f1c3c3e94ecd9f2ab285258603d4
ACR-f93bca3f4f234eea8b3069bfc3260074
ACR-75d401d0909540c595d5959c4c0528d9
ACR-980871dedfe64f179b2f13b859d46efa
ACR-384a068eb3aa48d4a93bc7d46f0d7a23
ACR-65a6e29064a449d7a69a3451568ffee3
ACR-9a7e68f1d35943009aad02eb40df15bf
ACR-2ac6271702014ba382f7d5e9921f785d
ACR-eac03f63ae634e93bcf97e269cbad9e5
ACR-7c337be2df5b46408f48341c75ad9cde
ACR-be5ad14f877549ee93921e2aae1a45c6
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
