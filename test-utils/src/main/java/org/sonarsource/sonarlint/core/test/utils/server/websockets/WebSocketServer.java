/*
ACR-dd90a6c1ebc14263ae4d673ee026d6b0
ACR-99027c0da86e4c038de532137324290a
ACR-5e37aa9f4dd145068f4c6e8d1ead017a
ACR-8259d6bcb1ce45d796770dca9ba2bb14
ACR-5f98ba596f3c444284cc64fd4ff334d1
ACR-c8ab13e6b1764bb98a6320122780d9bd
ACR-b3fc16788554478dbd67fdb4901eca86
ACR-4ee07084e85c44fd898cc11d7223f60f
ACR-60edefb4f0b64e7b81544d9b36b631f7
ACR-a6b523ef7e8a4daeb66eab09a5bbcfd9
ACR-15cfe1bc28374f72b52c807f63b03e44
ACR-503921e6dfec467597b353fedaa77350
ACR-d926ad7878c04a03be31836f48df865b
ACR-b953bc3c978943fc88f97e90d54f281d
ACR-ade5f8742ed24e92880a4504063b38b1
ACR-2d975c4eee0149408cbf1739c3f83aa6
ACR-ca1f0ece2782432fab6f2040f93826d3
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
      //ACR-e2676bc68ce340fab64305ca0a9e3e5f
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
