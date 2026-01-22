/*
ACR-88d499a592224e419d1d3b6b644a2373
ACR-d280b042dd3d47448d0470b3e2c4d037
ACR-f5ea7fe7e8ae4765b2125a0aac989adb
ACR-4581519066a54931b1aa4266fa6a616b
ACR-6247924a80304ae59e1aab9e03bdb4b7
ACR-946dd1602ae14afca01f41f6246e9c72
ACR-f9f6f3901bd24c749d6bcea476afbb5b
ACR-8b1e320840544fa9aac877f46fe9c29a
ACR-5a9b9ed730c147d8a218fc738e3e67be
ACR-a861cc0744174229908cfd98e4bab5f7
ACR-d9476798d33f4e9db96155f3b26c36de
ACR-1b9cc3c7560148b0a703bcb4559c58c0
ACR-41226d66d5ce400983ae180076847103
ACR-c63b205a82f04af39a9fb02beb430f23
ACR-8aecf365544d4eea91f06d02616cb7d0
ACR-39878948c2824c97a9a318ac9557865d
ACR-8eb836fb4bc94c74957ad3104fb064c7
 */
package org.sonarsource.sonarlint.core.test.utils.server.sse;

import java.io.File;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class SSEServer {

  public static final int DEFAULT_PORT = 54321;
  private Tomcat tomcat;
  private SSEServlet sseServlet;

  public void start() {
    try {
      var baseDir = new File("").getAbsoluteFile().getParentFile().getPath();
      tomcat = new Tomcat();
      tomcat.setBaseDir(baseDir);
      tomcat.setPort(DEFAULT_PORT);
      var context = tomcat.addContext("", baseDir);
      sseServlet = new SSEServlet();
      Tomcat.addServlet(context, "sse", sseServlet).addMapping("/");
      //ACR-33db5734be904fcc98f50f4aea891c85
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
    return "http://localhost:" + DEFAULT_PORT;
  }

  public void sendEventToAllClients(String eventPayload) {
    sseServlet.sendEventToAllClients(eventPayload);
  }

}
