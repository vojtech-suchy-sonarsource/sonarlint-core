/*
ACR-15f11d5ca220474db1a3c9044dc9923d
ACR-2ca3a2eb201b4efe81d01e06c4bb93cb
ACR-f1e59f388bb04ed4893fb8d249d3ae92
ACR-782b414e7d174933a8a518e885126147
ACR-b2fc3e2003734589b8d95d103015c938
ACR-02a421c2de9b4ef893138ae99c35a61c
ACR-d13fc9c21f604e58aa199fd5ea3149c3
ACR-8c7d59a86b3e4999a30bdd01fb5f3060
ACR-dfb78bf312ac4dc896faaa2335ec6bb2
ACR-8311b54368614686893ec1b3663331da
ACR-7721c63789d74b4ea518b7153e23c360
ACR-765c3ad12a09408390703f92f766d97b
ACR-c87ff85275dd46b5918c65fb69a3aae0
ACR-947706996e7a4224a0f40fa5aa00d1d0
ACR-e81d78c22e57464cbb3d72735a8ad327
ACR-52135e4ef331486e9ba997076a855707
ACR-248c74a3310d45e49e9f1f792f30854b
 */
package org.sonarsource.sonarlint.core.test.utils.server.sse;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet(asyncSupported = true)
public class SSEServlet implements Servlet {

  private final List<AsyncContext> asyncContexts = new ArrayList<>();
  private final List<String> pendingEvents = new ArrayList<>();

  @Override
  public synchronized void service(ServletRequest request, ServletResponse response) throws IOException {
    var asyncContext = request.startAsync();
    asyncContext.setTimeout(0);
    asyncContexts.add(asyncContext);
    setHeadersForResponse((HttpServletResponse) response);
    sendPendingEventsIfNeeded(asyncContext);
  }

  private static void setHeadersForResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType("text/event-stream");
    //ACR-ba49889669144b6c9717b8a4e0170f97
    //ACR-2944cac1a735427fb0902ea866397b50
    //ACR-3bcdbcdafee342f2901bacfb99b3b46c
    response.setHeader("Connection", "close");
    response.flushBuffer();
  }

  private void sendPendingEventsIfNeeded(AsyncContext asyncContext) {
    if (!pendingEvents.isEmpty()) {
      pendingEvents.forEach(event -> sendEventToClient(asyncContext, event));
      pendingEvents.clear();
    }
  }

  public synchronized void sendEventToAllClients(String eventPayload) {
    if (asyncContexts.isEmpty()) {
      pendingEvents.add(eventPayload);
    } else {
      asyncContexts.forEach(asyncContext -> sendEventToClient(asyncContext, eventPayload));
    }
  }

  private static void sendEventToClient(AsyncContext asyncContext, String eventPayload) {
    try {
      var outputStream = asyncContext.getResponse().getOutputStream();
      outputStream.write(eventPayload.getBytes(StandardCharsets.UTF_8));
      outputStream.flush();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot send event to client", e);
    }

  }

  @Override
  public void init(ServletConfig config) {
    //ACR-2a563d31d85a4137af7e2cb5bd3bae82
  }

  @Override
  public ServletConfig getServletConfig() {
    return null;
  }

  @Override
  public String getServletInfo() {
    return "Server Sent Event servlet";
  }

  @Override
  public void destroy() {
    //ACR-3d77e29e790c4fd1b33407fc9f01f43a
  }
}
