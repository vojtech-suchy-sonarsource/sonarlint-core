/*
ACR-90561a8ccceb4fbca8810b7c1f842479
ACR-69b48341aa374d53abd969003e2a8895
ACR-f1bdedcd415b40e0868c4b00246dc73e
ACR-63379dc6f617462f81539c187d65e496
ACR-856875c5212f42b488e33e77a71fae94
ACR-726257cb803c471380ffb5f0fe12d513
ACR-49ecefd4dbf64e76abaab5dda876d763
ACR-9dd3a973ba7545afb20a332aa04e1821
ACR-abda316bb897473eaf668cd3b49b3486
ACR-573f13b478cd4c15b398c073f3315353
ACR-637a8e901df34cb2b17481cbcf2c5a86
ACR-e26815ae8d084d05b4ffd4a9ee84d700
ACR-7df47b5e5bdf4057922d2e94bf9f51fa
ACR-b1859572b46f48aa9988dd9e35203495
ACR-d58c2e527586476f8a67a2681a34571b
ACR-39c81fe6b7db418dafabd09c8225d16c
ACR-e85d58b5a71f44c1a189c9cc4aaf23ac
 */
package org.sonarsource.sonarlint.core.embedded.server.filter;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpFilterChain;
import org.apache.hc.core5.http.io.HttpFilterHandler;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;

public class RateLimitFilter implements HttpFilterHandler {

  private static final int MAX_REQUESTS_PER_ORIGIN = 10;
  private static final long TIME_FRAME_MS = TimeUnit.SECONDS.toMillis(10);
  private final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

  @Override
  public void handle(ClassicHttpRequest request, HttpFilterChain.ResponseTrigger responseTrigger, HttpContext context, HttpFilterChain chain)
    throws HttpException, IOException {
    var originHeader = request.getHeader("Origin");
    var origin = originHeader != null ? originHeader.getValue() : null;
    if (origin == null) {
      var response = new BasicClassicHttpResponse(HttpStatus.SC_BAD_REQUEST);
      responseTrigger.submitResponse(response);
    } else {
      if (!isRequestAllowed(origin)) {
        var response = new BasicClassicHttpResponse(HttpStatus.SC_TOO_MANY_REQUESTS);
        responseTrigger.submitResponse(response);
      } else {
        context.setAttribute(AttributeUtils.ORIGIN_ATTRIBUTE, origin);
        chain.proceed(request, responseTrigger, context);
      }
    }
  }

  private boolean isRequestAllowed(String origin) {
    long currentTime = System.currentTimeMillis();
    var counter = requestCounters.computeIfAbsent(origin, k -> new RequestCounter(currentTime));
    requestCounters.compute(origin, (k, v) -> {
      if (currentTime - counter.timestamp > TIME_FRAME_MS) {
        counter.timestamp = currentTime;
        counter.count = 1;
      } else {
        counter.count++;
      }
      return counter;
    });
    return counter.count <= MAX_REQUESTS_PER_ORIGIN;
  }

  private static class RequestCounter {
    long timestamp;
    int count;

    RequestCounter(long timestamp) {
      this.timestamp = timestamp;
      this.count = 0;
    }
  }

}
