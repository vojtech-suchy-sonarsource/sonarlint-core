/*
ACR-8b12ad66186d411ba351b985b07ca71f
ACR-b34db9aaf2514af4ad2c88576d25d510
ACR-88effef416e74f168af019b20763605e
ACR-93b4840908154e55be83ffe9df2fdabd
ACR-53ba0c5ebe3e40b1b2559dcaaad5cccd
ACR-74b5e9d621bf46baabefae7c84930685
ACR-833564a53cc043cfa622842a71fb6c5e
ACR-e15cd135c78d45b9a8c6fbe39fa5fc5b
ACR-6970120744ef4e1f89cef2135e06c708
ACR-b83128cb159b466ab9c3c56ea4fc82ca
ACR-dc397145a1be42db9ba876ef5689c03c
ACR-a4eb6611f7484cc09638e68d7089426f
ACR-be7930b9031747238b7ec9221bd4a5be
ACR-a2ce44b76d3f451dbeff5cae805cf260
ACR-5b56d107d33f4424bf0307e75e2938df
ACR-b881da494f0d4968a9caacbb525f7498
ACR-936dce852de64203b6921c662d892e71
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
      //ACR-e09cc2610de840449d412a80f2d318ec
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
