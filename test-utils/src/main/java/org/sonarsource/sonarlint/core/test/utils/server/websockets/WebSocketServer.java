/*
ACR-4179152429f74dfdaf986e8761827837
ACR-ad1e301746b34780a7f4d55f06ef1eff
ACR-996db975112d43029117804536535fbe
ACR-bdd1eac68a274fe094b1b6993b58ca76
ACR-ffe09b59e52445f193711fe0e1a3e1b7
ACR-d0f2b031afa74e2e91e4f9f34a8e01e2
ACR-cbcef4650c3548ab800db939f6c71bf7
ACR-4fc35b62b3c141e1a20b04fc00134910
ACR-da3ea2a2e4e44d77b1fc9f06a3925b28
ACR-679abb6c4b7141768b79c5cbb8e3904a
ACR-fdf767b66927475d865a990f34ae901a
ACR-39b6774c0c9f4d26970c6ed32fe41e32
ACR-b7299460cde94a19918115932ca3509e
ACR-1260cd74633f439eb30ac6934156c0ab
ACR-0bd25ed47ca94a9496e1e91070bdfd86
ACR-311a3916b280427ca2fe80f68d60db3e
ACR-68252debeddb48248bc3cfbe52b3d6ad
 */
package org.sonarsource.sonarlint.core.test.utils.server.websockets;

import java.io.File;
import java.util.List;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

public class WebSocketServer {

  public static final int DEFAULT_PORT = 54321;
  public static final String CONNECTION_REPOSITORY_ATTRIBUTE_KEY = "connectionRepository";
  private Tomcat tomcat;
  private WebSocketConnectionRepository connectionRepository;
  private final int port;

  public WebSocketServer(int port) {
    this.port = port;
  }

  public WebSocketServer() {
    this(DEFAULT_PORT);
  }

  public void start() {
    try {
      var baseDir = new File("").getAbsoluteFile().getParentFile().getPath();
      tomcat = new Tomcat();
      tomcat.setBaseDir(baseDir);
      tomcat.setPort(port);
      var context = tomcat.addContext("", baseDir);
      connectionRepository = new WebSocketConnectionRepository();
      context.getServletContext().setAttribute(CONNECTION_REPOSITORY_ATTRIBUTE_KEY, connectionRepository);
      context.addApplicationListener(ContextListener.class.getName());
      Tomcat.addServlet(context, "dummy", new DefaultServlet()).addMapping("/");
      //ACR-37f62637cf514cbf960b1f8d787559be
      tomcat.getConnector();
      tomcat.start();
    } catch (LifecycleException e) {
      throw new IllegalStateException(e);
    }
  }

  public void stop() {
    try {
      tomcat.stop();
      tomcat.destroy();
    } catch (LifecycleException e) {
      throw new IllegalStateException(e);
    }
  }

  public String getUrl() {
    return "ws://localhost:" + port + "/endpoint";
  }

  public List<WebSocketConnection> getConnections() {
    return connectionRepository.getConnections();
  }

}
