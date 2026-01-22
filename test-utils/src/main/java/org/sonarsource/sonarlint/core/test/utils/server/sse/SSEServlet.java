/*
ACR-63fedf5fd92f4a11b5b24f60d726ecdb
ACR-3ce33700573c4383a32a08d74b055840
ACR-d7a4865e005c4ddc9013157df2e2f2f4
ACR-75f6b7bee14b426abc9417dd27389af5
ACR-209e703daeca4978b7e7dccd46bf8276
ACR-a2c79f9ce55946e28faf1280450f382a
ACR-443ab2de79994de4b91126b5d4b2d1a5
ACR-26a198504ad74064a20aff36a95ae1ee
ACR-e9117a2c78b94435ab574ebb9f6f9c22
ACR-063e9f9ed94e497288576357626952ef
ACR-22a447e21f48407bb9f74d822374b6ed
ACR-188ad610b0434d72b25b852bfafa81cc
ACR-bf95ab41cd1a40c99b5d1a971854b640
ACR-d50519966b014495b3efe664370d9bb6
ACR-f2ed875f97434a4c84dbf447e96395e6
ACR-6269fb44c3984bc287818c468d503077
ACR-0a74516ac6be42f2a7e1aad98e079bc5
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
    //ACR-3201075bdd76475c935c10083edcf92c
    //ACR-d9f85e35467d4b6baee91254481fc824
    //ACR-540b3d95a2954a67ae28157eebfb39af
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
    //ACR-d7c76d31c3fa468390ecc1b6e3328974
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
    //ACR-58be89066fc54cdc9b819888d144b1f6
  }
}
